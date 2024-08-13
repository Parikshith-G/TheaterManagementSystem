package com.theater.booking.service.implementation;

import com.theater.booking.dto.BookingDto;
import com.theater.booking.dto.EmailWrapper;
import com.theater.booking.dto.MovieDto;
import com.theater.booking.dto.RoomDto;
import com.theater.booking.dto.ShowDto;
import com.theater.booking.dto.TheaterDto;
import com.theater.booking.entity.Booking;
import com.theater.booking.entity.Seat;
import com.theater.booking.exceptions.AppException;
import com.theater.booking.feign.FeignMovieCall;
import com.theater.booking.feign.FeignEmailCall;
import com.theater.booking.feign.FeignRoomCall;
import com.theater.booking.feign.FeignShowCall;
import com.theater.booking.feign.FeignTheaterCall;
import com.theater.booking.repository.BookingRepository;
import com.theater.booking.service.contract.IBookingService;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

import java.util.UUID;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Service
public class BookingService implements IBookingService {

	private static final Logger logger = LoggerFactory.getLogger(BookingService.class);

	private final BookingRepository repo;

	private final FeignRoomCall roomCall;

	private final FeignShowCall showCall;

	private final FeignEmailCall emailCall;

	private final FeignTheaterCall theaterCall;

	private final FeignMovieCall movieCall;

	@Autowired
	public BookingService(BookingRepository repo, FeignRoomCall roomCall, FeignShowCall showCall,
			FeignEmailCall emailCall, FeignTheaterCall theaterCall, FeignMovieCall movieCall) {
		this.repo = repo;
		this.roomCall = roomCall;
		this.showCall = showCall;
		this.emailCall = emailCall;
		this.theaterCall = theaterCall;
		this.movieCall = movieCall;

	}

	@Override
	public ResponseEntity<byte[]> createBooking(BookingDto bookingDto) {
		logger.info("Creating booking for user: {}", bookingDto.userMail());

		ShowDto show = showCall.getShowById(bookingDto.showId()).getBody();

		Integer availableSeats = 0;
		if (show == null) {
			logger.error("Show not found for ID: {}", bookingDto.showId());
			throw new AppException("Show not found(Booking service))", HttpStatus.NOT_FOUND);
		}

		RoomDto room = roomCall.getRoomById(show.roomId()).getBody();

		if (room == null) {
			logger.error("Room not found for ID: {}", show.roomId());
			throw new AppException("Room not found(Booking service))", HttpStatus.NOT_FOUND);
		}
		availableSeats = roomCall.getAvailableSeatOfRoom(room.id()).getBody();

		if (availableSeats == null || availableSeats == 0) {
			logger.error("No available seats for room ID: {}", room.id());
			throw new AppException("Room Full(Booking service)", HttpStatus.BAD_REQUEST);
		}

		roomCall.updateSeatBooked(show.roomId(), bookingDto.seatId());
		LocalDateTime now = LocalDateTime.now();
		String uuid = UUID.randomUUID().toString();
		BookingDto newDto = new BookingDto(bookingDto.id(), bookingDto.userMail(), bookingDto.showId(), now,
				bookingDto.showTime(), bookingDto.seatId(), uuid, bookingDto.status(), now, now, bookingDto.userName(),
				bookingDto.seatName()

		);

		Booking booking = bookingDtotoBooking(newDto);
		booking.setSeatId(bookingDto.seatId());

		roomCall.updateRoom(room.id(), room);

		TheaterDto theaterDto = theaterCall.getTheaterById(room.theaterId()).getBody();
		if (theaterDto == null) {
			logger.info("Theater was null: {}", HttpStatus.NOT_FOUND);
			throw new AppException("Theater not found", HttpStatus.NOT_FOUND);
		}
		MovieDto movieDto = movieCall.getMovieById(show.movieId());
		Seat seat = roomCall.findSeatById(Long.parseLong(bookingDto.seatId()));

		Booking savedBooking = repo.save(booking);
		logger.info("Booking saved with reference: {}", savedBooking.getBookingReference());
		EmailWrapper wrapper = new EmailWrapper(theaterDto.name(), theaterDto.location(), room.name(), "Placeolder",
				show.startTime(), show.endTime(), bookingDto.userName(), movieDto.title(), seat.getSeatNumber(),
				savedBooking.getBookingReference(), bookingDto.userMail(), show.price());
		return emailCall.generatePdf(wrapper);

	}

	private BookingDto bookingToBookingDto(Booking savedBooking) {
		LocalDateTime now = LocalDateTime.now();
		return new BookingDto(savedBooking.getId(), savedBooking.getUserMail(), savedBooking.getShowId(),
				savedBooking.getBookingDate(), savedBooking.getShowTime(), savedBooking.getSeatId(),
				savedBooking.getBookingReference(), savedBooking.getStatus(), savedBooking.getCreatedAt(), now, "", ""

		);
	}

	private Booking bookingDtotoBooking(BookingDto bookingDto) {
		LocalDateTime now = LocalDateTime.now();
		return Booking.builder().userMail(bookingDto.userMail()).showId(bookingDto.showId())
				.bookingDate(bookingDto.bookingDate()).showTime(bookingDto.showTime())
				.bookingReference(bookingDto.bookingReference()).status(bookingDto.status())
				.createdAt(bookingDto.createdAt()).updatedAt(now).build();
	}

	@Override
	public BookingDto getBookingById(Long id) {
		logger.info("Fetching booking with ID: {}", id);
		Booking booking = repo.findById(id)
				.orElseThrow(() -> new AppException("Booking not found", HttpStatus.NOT_FOUND));

		return bookingToBookingDto(booking);

	}

	@Override
	public BookingDto updateBooking(Long id, BookingDto bookingDto) {
		logger.info("Updating booking with ID: {}", id);
		Booking booking = repo.findById(id)
				.orElseThrow(() -> new AppException("Booking not found", HttpStatus.NOT_FOUND));

		booking.setUserMail(bookingDto.userMail());
		booking.setShowId(bookingDto.showId());
		booking.setBookingDate(bookingDto.bookingDate());
		booking.setShowTime(bookingDto.showTime());
		booking.setSeatId(bookingDto.seatId());
		booking.setStatus(bookingDto.status());
		booking.setUpdatedAt(LocalDateTime.now());
		Booking updatedBooking = repo.save(booking);
		logger.info("Booking updated with reference: {}", updatedBooking.getBookingReference());
		return bookingToBookingDto(updatedBooking);

	}

	@Override
	public void deleteBooking(Long id) {
		logger.info("Deleting booking with ID: {}", id);
		Booking booking = repo.findById(id)
				.orElseThrow(() -> new AppException("Booking not found", HttpStatus.NOT_FOUND));
		repo.delete(booking);
		logger.info("Booking deleted with ID: {}", id);
	}
}
