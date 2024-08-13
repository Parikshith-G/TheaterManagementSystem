package com.theater.service.implementation;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.theater.dtos.RoomDto;
import com.theater.dtos.SeatStatusDto;
import com.theater.entities.Room;
import com.theater.entities.Seat;
import com.theater.entities.Show;
import com.theater.entities.Theater;
import com.theater.exception.AppException;
import com.theater.repository.RoomsRepository;
import com.theater.repository.SeatRepository;
import com.theater.repository.ShowRepository;
import com.theater.repository.TheaterRepository;
import com.theater.service.contract.IRoomsService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class RoomsService implements IRoomsService {

	private static final Logger logger = LoggerFactory.getLogger(RoomsService.class);

	private final RoomsRepository roomRepository;
	private final TheaterRepository theaterRepository;
	private final ShowRepository showRepository;
	private final SeatRepository seatRepository;

	@Autowired
	public RoomsService(RoomsRepository roomRepository, TheaterRepository theaterRepository,
			ShowRepository showRepository, SeatRepository seatRepository) {
		this.roomRepository = roomRepository;
		this.seatRepository = seatRepository;
		this.theaterRepository = theaterRepository;
		this.showRepository = showRepository;
	}

	@Override
	public List<RoomDto> getAllRooms() {
		logger.info("Fetching all rooms");
		List<Room> rooms = roomRepository.findAll();
		logger.info("Fetched {} rooms", rooms.size());
		return rooms.stream().map(this::convertToDTO).collect(Collectors.toList());
	}

	@Override
	public RoomDto getRoomById(Long id) {
		logger.info("Fetching room with ID: {}", id);
		Optional<Room> optionalRoom = roomRepository.findById(id);
		if (optionalRoom.isPresent()) {
			logger.info("Room found with ID: {}", id);
			return convertToDTO(optionalRoom.get());
		}
		logger.error("Room not found with ID: {}", id);
		throw new AppException("Room not found with id(room service): " + id, HttpStatus.NOT_FOUND);
	}

	@Override
	public RoomDto createRoom(RoomDto roomDTO) {
		logger.info("Creating room with name: {}", roomDTO.name());
		Room room = convertToEntity(roomDTO);
		Optional<Theater> optionalTheater = theaterRepository.findById(roomDTO.theaterId());
		if (optionalTheater.isPresent()) {
			room.setTheater(optionalTheater.get());
			Room savedRoom = roomRepository.save(room);
			List<Seat> seats = createSeats(roomDTO.capacity(), savedRoom);
			savedRoom.setSeats(seats);
			savedRoom = roomRepository.save(savedRoom);
			logger.info("Created room with ID: {}", savedRoom.getId());
			return convertToDTO(savedRoom);
		}
		logger.error("Theater not found with ID: {}", roomDTO.theaterId());
		throw new AppException("Theater not found with id(room service): " + roomDTO.theaterId(), HttpStatus.NOT_FOUND);
	}

	private List<Seat> createSeats(int capacity, Room room) {
		logger.info("Creating {} seats for room with ID: {}", capacity, room.getId());
		List<Seat> seats = new ArrayList<>();
		for (int i = 1; i <= capacity; i++) {
			Seat seat = new Seat();
			seat.setRoom(room);
			seat.setSeatNumber("Seat-" + i);
			seat.setBooked(false);
			seats.add(seat);
		}
		List<Seat> savedSeats = seatRepository.saveAll(seats);
		room.setSeats(savedSeats);
		logger.info("Created {} seats for room with ID: {}", savedSeats.size(), room.getId());
		return savedSeats;
	}

	@Override
	public RoomDto updateRoom(Long id, RoomDto roomDTO) {
		logger.info("Updating room with ID: {}", id);
		Optional<Room> optionalRoom = roomRepository.findById(id);
		if (optionalRoom.isPresent()) {
			Room room = optionalRoom.get();
			BeanUtils.copyProperties(roomDTO, room, "id", "theaterId");
			Optional<Theater> optionalTheater = theaterRepository.findById(roomDTO.theaterId());
			if (optionalTheater.isPresent()) {
				room.setTheater(optionalTheater.get());
				Room updatedRoom = roomRepository.save(room);
				logger.info("Updated room with ID: {}", updatedRoom.getId());
				return convertToDTO(updatedRoom);
			}
			logger.error("Theater not found with ID: {}", roomDTO.theaterId());
			throw new AppException("Theater not found with id(room service): " + roomDTO.theaterId(),
					HttpStatus.NOT_FOUND);
		}
		logger.error("Room not found with ID: {}", id);
		throw new AppException("Room not found with id(room service): " + id, HttpStatus.NOT_FOUND);
	}

	@Override
	public void deleteRoom(Long id) {
		logger.info("Deleting room with ID: {}", id);
		Optional<Room> optionalRoom = roomRepository.findById(id);
		if (optionalRoom.isPresent()) {
			List<Show> shows = optionalRoom.get().getShows();
			if (shows.isEmpty()) {
				throw new AppException("No shows found for the room", HttpStatus.NOT_FOUND);
			}
			Show show = showRepository.findById(shows.get(0).getId())
					.orElseThrow(() -> new AppException("Show not found", HttpStatus.NOT_FOUND));
			if (show.getStartTime().isAfter(LocalDateTime.now())) {
				throw new AppException("Room cant be deleted", HttpStatus.UNAUTHORIZED);
			}
			roomRepository.deleteById(id);
			List<Seat> seats = seatRepository.findAll().stream().filter(s -> s.getRoom().getId() == id).toList();
			seatRepository.deleteAll(seats);

			logger.info("Deleted room with ID: {}", id);
		} else {
			logger.error("Room not found with ID: {}", id);
			throw new AppException("Room not found with id(room service): " + id, HttpStatus.NOT_FOUND);
		}
	}

	@Override
	public Integer getAvailableSeatsofRoom(Long roomId) {
		logger.info("Getting available seats for room with ID: {}", roomId);
		Optional<Room> optionalRoom = roomRepository.findById(roomId);
		if (optionalRoom.isPresent()) {
			Room room = optionalRoom.get();
			int availableSeats = room.getCapacity() - room.getSeats().stream().filter(Seat::isBooked).toList().size();
			logger.info("Available seats for room with ID {}: {}", roomId, availableSeats);
			return availableSeats;
		}
		logger.error("Room not found with ID: {}", roomId);
		throw new AppException("Room not found with id: " + roomId, HttpStatus.NOT_FOUND);
	}

	@Override
	public Room exposedConverterDtoToObj(RoomDto dto) {
		return convertToEntity(dto);
	}

	private RoomDto convertToDTO(Room room) {
		List<Show> shows = room.getShows();
		List<Long> showIds = shows.stream().map(Show::getId).toList();

		List<Seat> seats = room.getSeats();
		List<SeatStatusDto> seatDtos = seats.stream().map(this::converter).toList();

		return new RoomDto(room.getId(), room.getTheater().getId(), room.getName(), room.getCapacity(),
				room.getAvailableSeats(), showIds, seatDtos);
	}

	private Room convertToEntity(RoomDto roomDTO) {
		Theater theater = theaterRepository.findById(roomDTO.theaterId())
				.orElseThrow(() -> new AppException("Theater not found(Room Service)", HttpStatus.NOT_FOUND));

		List<Show> shows = showRepository.findAll();
		Set<Long> showIdxHolder = new HashSet<>(roomDTO.showIds());
		List<Show> setShows = shows.stream().filter(show -> showIdxHolder.contains(show.getId()))
				.collect(Collectors.toList());

		List<Seat> seats = seatRepository.findAll();
		Set<Long> seatIdxHolder = roomDTO.seatStatuses().stream().map(SeatStatusDto::seatId)
				.collect(Collectors.toSet());
		List<Seat> setSeat = seats.stream().filter(seat -> seatIdxHolder.contains(seat.getId()))
				.collect(Collectors.toList());

		return Room.builder().id(roomDTO.id()).theater(theater).name(roomDTO.name()).capacity(roomDTO.capacity())
				.shows(setShows).seats(setSeat).availableSeats(roomDTO.availableSeats()).build();
	}

	@Override
	public Seat findSeatById(Long seatId) {
		logger.info("Finding seat with ID: {}", seatId);
		return seatRepository.findById(seatId)
				.orElseThrow(() -> new AppException("Seat not found(Room service)", HttpStatus.NOT_FOUND));
	}

	private SeatStatusDto converter(Seat seat) {
		return new SeatStatusDto(seat.getId(), seat.isBooked());
	}

	@Override
	public Boolean updateSeatBooked(Long roomId, Long seatNumber) {
		logger.info("Updating seat booked status for seat ID: {} in room ID: {}", seatNumber, roomId);
		Room room = roomRepository.findById(roomId)
				.orElseThrow(() -> new AppException("Room not found(Room Service)", HttpStatus.NOT_FOUND));

		List<Seat> allSeats = room.getSeats();
		for (Seat s : allSeats) {
			if (s.getId().equals(seatNumber)) {
				s.setBooked(true);
				seatRepository.save(s);
				break;
			}
		}
		room.setSeats(allSeats);
		int availableSeats = room.getCapacity() - (int) allSeats.stream().filter(Seat::isBooked).count();
		room.setAvailableSeats(availableSeats);
		roomRepository.save(room);
		logger.info("Updated available seats for room ID: {}", roomId);
		return true;
	}

	public List<RoomDto> getAllRoomsByTheaterId(Long theaterId) {
		logger.info("Fetching all rooms for theater ID: {}", theaterId);
		List<Room> rooms = roomRepository.findAll();
		logger.info("Fetched {} rooms for theater ID: {}", rooms.size(), theaterId);
		return rooms.stream().filter(room -> Objects.equals(room.getTheater().getId(), theaterId))
				.map(this::convertToDTO).collect(Collectors.toList());
	}

	public void generateRoomsReport(String filePath) throws IOException {
		logger.info("Generating rooms report to file: {}", filePath);
		List<Room> rooms = roomRepository.findAll();
		List<RoomDto> roomDtos = rooms.stream().map(this::convertToDTO).collect(Collectors.toList());
		try (FileWriter writer = new FileWriter(filePath)) {
			new ObjectMapper().writeValue(writer, roomDtos);
			logger.info("Rooms report generated successfully to file: {}", filePath);
		}
	}
}