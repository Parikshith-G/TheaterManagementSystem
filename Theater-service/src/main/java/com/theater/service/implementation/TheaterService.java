package com.theater.service.implementation;

import com.theater.dtos.RoomDto;
import com.theater.dtos.SeatStatusDto;
import com.theater.dtos.TheaterDto;
import com.theater.entities.Room;
import com.theater.entities.Show;
import com.theater.entities.Theater;
import com.theater.exception.AppException;
import com.theater.repository.RoomsRepository;

import com.theater.repository.TheaterRepository;
import com.theater.service.contract.ITheaterService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service

public class TheaterService implements ITheaterService {

    private static final Logger logger = LoggerFactory.getLogger(TheaterService.class);

    private final TheaterRepository theaterRepository;
    private final RoomsRepository roomsRepository;
  

    @Autowired
    public TheaterService(TheaterRepository theaterRepository, RoomsRepository roomsRepository) {
        this.theaterRepository = theaterRepository;
        this.roomsRepository = roomsRepository;
      
    }

    @Override
    public List<TheaterDto> getAllTheaters() {
        logger.info("Fetching all theaters");
        List<Theater> theaters = theaterRepository.findAll();
        return theaters.stream().map(this::theaterToTheaterDto).toList();
    }

    @Override
    public TheaterDto getTheaterById(Long id) {
        logger.info("Fetching theater with ID: {}", id);
        Theater theater = theaterRepository.findById(id)
                .orElseThrow(() -> new AppException("Theater not found (theater service)", HttpStatus.NOT_FOUND));
        return theaterToTheaterDto(theater);
    }

    @Override
    public TheaterDto createTheater(TheaterDto theaterDTO) {
        logger.info("Creating theater");
        Theater theater = theaterDtoToTheater(theaterDTO);
        Theater savedTheater = theaterRepository.save(theater);
        return theaterToTheaterDto(savedTheater);
    }

    @Override
    public TheaterDto updateTheater(Long id, TheaterDto theaterDTO) {
        logger.info("Updating theater with ID: {}", id);
        Theater theater = theaterRepository.findById(id)
                .orElseThrow(() -> new AppException("Theater not found (theater service)", HttpStatus.NOT_FOUND));

        List<Long> roomIdx = theaterDTO.roomsIdx();
        List<Room> rooms = new ArrayList<>();
        for (Long idx : roomIdx) {
            Room room = roomsRepository.findById(idx)
                    .orElseThrow(() -> new AppException("Room not found (theater service)", HttpStatus.NOT_FOUND));
            rooms.add(room);
        }

        theater.setName(theaterDTO.name());
        theater.setLocation(theaterDTO.location());
        theater.setRooms(rooms);

        Theater savedTheater = theaterRepository.save(theater);
        return theaterToTheaterDto(savedTheater);
    }

    @Override
    public void deleteTheater(Long id) {
        logger.info("Deleting theater with ID: {}", id);
        Theater theater = theaterRepository.findById(id)
                .orElseThrow(() -> new AppException("Theater not found (theater service)", HttpStatus.NOT_FOUND));
        theaterRepository.delete(theater);
    }

    @Override
    public TheaterDto addRoomToTheater(Long theaterId, Long roomId) {
        logger.info("Adding room with ID {} to theater with ID {}", roomId, theaterId);
        Theater theater = theaterRepository.findById(theaterId)
                .orElseThrow(() -> new AppException("Theater not found (theater service)", HttpStatus.NOT_FOUND));
        Room room = roomsRepository.findById(roomId)
                .orElseThrow(() -> new AppException("Room not found (theater service)", HttpStatus.NOT_FOUND));

        List<Room> rooms = theater.getRooms();
        if (!rooms.contains(room)) {
            rooms.add(room);
            theater.setRooms(rooms);
        }

        Theater savedTheater = theaterRepository.save(theater);
        return theaterToTheaterDto(savedTheater);
    }

    @Override
    public List<RoomDto> getRoomsInTheater(Long theaterId) {
        logger.info("Fetching rooms in theater with ID: {}", theaterId);
        Theater theater = theaterRepository.findById(theaterId)
                .orElseThrow(() -> new AppException("Theater not found", HttpStatus.NOT_FOUND));

        List<Room> rooms = theater.getRooms();
        return rooms.stream().map(this::roomToRoomDto).toList();
    }

    private Theater theaterDtoToTheater(TheaterDto dto) {
        List<Long> roomIdx = dto.roomsIdx();
        List<Room> rooms = new ArrayList<>();
        for (Long idx : roomIdx) {
            Room room = roomsRepository.findById(idx)
                    .orElseThrow(() -> new AppException("Room not found (theater service)", HttpStatus.NOT_FOUND));
            rooms.add(room);
        }
        return new Theater(dto.id(), dto.name(), dto.location(), rooms);
    }

    private TheaterDto theaterToTheaterDto(Theater theater) {
        List<Room> rooms = theater.getRooms();
        List<Long> roomIdx = rooms.stream().map(Room::getId).toList();
        return new TheaterDto(theater.getId(), theater.getName(), theater.getLocation(), roomIdx);
    }

    private RoomDto roomToRoomDto(Room room) {
        List<Long> showIds = room.getShows().stream().map(Show::getId).toList();
        List<SeatStatusDto> seatStatuses = room.getSeats().stream()
                .map(seat -> new SeatStatusDto(seat.getId(), seat.isBooked()))
                .toList();

        return new RoomDto(
                room.getId(),
                room.getTheater().getId(),
                room.getName(),
                room.getCapacity(),
                room.getAvailableSeats(),
                showIds,
                seatStatuses
        );
    }
}
