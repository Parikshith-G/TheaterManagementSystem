package com.theater.booking.service.implementation;

import com.theater.booking.dto.*;
import com.theater.booking.entity.Booking;
import com.theater.booking.entity.Seat;
import com.theater.booking.exceptions.AppException;
import com.theater.booking.feign.*;
import com.theater.booking.repository.BookingRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class BookingServiceTest {

	@Mock
	private BookingRepository repo;

	@Mock
	private FeignRoomCall roomCall;

	@Mock
	private FeignShowCall showCall;

	@Mock
	private FeignEmailCall emailCall;

	@Mock
	private FeignTheaterCall theaterCall;

	@Mock
	private FeignMovieCall movieCall;

	@InjectMocks
	private BookingService bookingService;

	private BookingDto bookingDto;
	private ShowDto showDto;
	private RoomDto roomDto;
	private TheaterDto theaterDto;
	private MovieDto movieDto;
	private Seat seat;

	@BeforeEach
	void setUp() {
		bookingDto = new BookingDto(1L, "user@example.com", 1L, LocalDateTime.now(), LocalDateTime.now(), "1",
				UUID.randomUUID().toString(), "CONFIRMED", LocalDateTime.now(), LocalDateTime.now(), "User Name", "A1");
		showDto = new ShowDto(1L, 1L, 1L, LocalDateTime.now(), LocalDateTime.now(), 100.0);
		roomDto = new RoomDto(1L, 1L, "Room 1", 100, 10, List.of(1L), List.of());
		theaterDto = new TheaterDto(1L, "Theater 1", "Location 1", List.of(1L));
		movieDto = new MovieDto(1L, "Movie 1", "Description 1", "Genre 1", LocalDate.now(), 120);
		seat = new Seat();
		seat.setId(1L);
		seat.setSeatNumber("A1");
		seat.setBooked(false);
		lenient().when(showCall.getShowById(1L)).thenReturn(new ResponseEntity<>(showDto, HttpStatus.OK));
		lenient().when(roomCall.getRoomById(1L)).thenReturn(new ResponseEntity<>(roomDto, HttpStatus.OK));
		lenient().when(roomCall.getAvailableSeatOfRoom(1L)).thenReturn(new ResponseEntity<>(10, HttpStatus.OK));
		lenient().when(roomCall.updateSeatBooked(1L, "1")).thenReturn(true); // Return Boolean value
		lenient().when(repo.save(any(Booking.class))).thenReturn(new Booking());
		lenient().when(theaterCall.getTheaterById(1L)).thenReturn(new ResponseEntity<>(theaterDto, HttpStatus.OK));
		lenient().when(movieCall.getMovieById(1L)).thenReturn(movieDto);
		lenient().when(roomCall.findSeatById(1L)).thenReturn(seat);
		lenient().when(emailCall.generatePdf(any(EmailWrapper.class)))
				.thenReturn(new ResponseEntity<>(new byte[0], HttpStatus.OK));
	}

	@Test
	void testCreateBookingTheaterNotFound() {
		Long showId = 1L;
		Long roomId = 1L;
		Long theaterId = 1L;
		Long movieId = 1L;
		Long bookingId = 1L;
		LocalDateTime now = LocalDateTime.now();
		String userMail = "test@example.com";
		String seatId = "A1";

		BookingDto bookingDto = new BookingDto(bookingId, userMail, showId, now, now, seatId, null, "CONFIRMED", now,
				now, "Test User", "Seat A1");

		ShowDto showDto = new ShowDto(showId, movieId, roomId, now, now.plusHours(2), 10.0);
		when(showCall.getShowById(showId)).thenReturn(ResponseEntity.ok(showDto));
		when(roomCall.getRoomById(roomId)).thenReturn(ResponseEntity.ok(roomDto));
		when(roomCall.getAvailableSeatOfRoom(roomId)).thenReturn(ResponseEntity.ok(1));
		when(theaterCall.getTheaterById(theaterId)).thenReturn(ResponseEntity.of(Optional.empty()));

		AppException exception = assertThrows(AppException.class, () -> bookingService.createBooking(bookingDto));
		assertEquals("Theater not found", exception.getMessage());
		assertEquals(HttpStatus.NOT_FOUND, exception.getStatus());

		verify(repo, never()).save(any(Booking.class));
		verify(emailCall, never()).generatePdf(any(EmailWrapper.class));
	}

	@Test
	void testCreateBookingSuccessfully() {
		when(showCall.getShowById(1L)).thenReturn(new ResponseEntity<>(showDto, HttpStatus.OK));
		when(roomCall.getRoomById(1L)).thenReturn(new ResponseEntity<>(roomDto, HttpStatus.OK));
		when(roomCall.getAvailableSeatOfRoom(1L)).thenReturn(new ResponseEntity<>(10, HttpStatus.OK));
		when(roomCall.updateSeatBooked(1L, "1")).thenReturn(true);
		when(repo.save(any(Booking.class))).thenReturn(new Booking());
		when(theaterCall.getTheaterById(1L)).thenReturn(new ResponseEntity<>(theaterDto, HttpStatus.OK));
		when(movieCall.getMovieById(1L)).thenReturn(movieDto);
		when(roomCall.findSeatById(1L)).thenReturn(seat);
		when(emailCall.generatePdf(any(EmailWrapper.class)))
				.thenReturn(new ResponseEntity<>(new byte[0], HttpStatus.OK));

		ResponseEntity<byte[]> response = bookingService.createBooking(bookingDto);
		assertEquals(HttpStatus.OK, response.getStatusCode());
	}

	@Test
	void testCreateBookingShowNotFound() {
		when(showCall.getShowById(1L)).thenReturn(new ResponseEntity<>(null, HttpStatus.NOT_FOUND));

		AppException exception = assertThrows(AppException.class, () -> bookingService.createBooking(bookingDto));
		assertEquals("Show not found(Booking service))", exception.getMessage());
	}

	@Test
	void testCreateBookingRoomNotFound() {
		when(showCall.getShowById(1L)).thenReturn(new ResponseEntity<>(showDto, HttpStatus.OK));
		when(roomCall.getRoomById(1L)).thenReturn(new ResponseEntity<>(null, HttpStatus.NOT_FOUND));

		AppException exception = assertThrows(AppException.class, () -> bookingService.createBooking(bookingDto));
		assertEquals("Room not found(Booking service))", exception.getMessage());
	}

	@Test
	void testCreateBookingNoAvailableSeats() {
		when(showCall.getShowById(1L)).thenReturn(new ResponseEntity<>(showDto, HttpStatus.OK));
		when(roomCall.getRoomById(1L)).thenReturn(new ResponseEntity<>(roomDto, HttpStatus.OK));
		when(roomCall.getAvailableSeatOfRoom(1L)).thenReturn(new ResponseEntity<>(0, HttpStatus.OK));

		AppException exception = assertThrows(AppException.class, () -> bookingService.createBooking(bookingDto));
		assertEquals("Room Full(Booking service)", exception.getMessage());
	}

	@Test
	void testGetBookingById() {
		Booking booking = new Booking(1L, "user@example.com", 1L, LocalDateTime.now(), LocalDateTime.now(), "1",
				UUID.randomUUID().toString(), "CONFIRMED", LocalDateTime.now(), LocalDateTime.now());
		when(repo.findById(1L)).thenReturn(Optional.of(booking));

		BookingDto result = bookingService.getBookingById(1L);
		assertEquals(booking.getId(), result.id());
	}

	@Test
	void testUpdateBookingSuccess() {
		Long bookingId = 1L;
		LocalDateTime now = LocalDateTime.now();

		Booking existingBooking = Booking.builder().id(bookingId).userMail("test@example.com").showId(1L)
				.bookingDate(now.minusDays(1)).showTime(now.minusDays(1)).seatId("A1").bookingReference("REF123")
				.status("CONFIRMED").createdAt(now.minusDays(2)).updatedAt(now.minusDays(1)).build();

		BookingDto updateDto = new BookingDto(bookingId, "new@example.com", 2L, now, now, "B2", "REF456", "UPDATED",
				now.minusDays(2), now.minusDays(1), "New User", "Seat B2");

		when(repo.findById(bookingId)).thenReturn(Optional.of(existingBooking));
		when(repo.save(any(Booking.class))).thenAnswer(invocation -> invocation.getArgument(0));

		BookingDto result = bookingService.updateBooking(bookingId, updateDto);

		assertNotNull(result);
		assertEquals("new@example.com", result.userMail());
		assertEquals(2L, result.showId());
		assertEquals(now, result.bookingDate());
		assertEquals(now, result.showTime());
		assertEquals("B2", result.seatId());
		assertEquals("UPDATED", result.status());

		ArgumentCaptor<Booking> bookingCaptor = ArgumentCaptor.forClass(Booking.class);
		verify(repo).save(bookingCaptor.capture());
		Booking savedBooking = bookingCaptor.getValue();

		assertEquals("new@example.com", savedBooking.getUserMail());
		assertEquals(2L, savedBooking.getShowId());
		assertEquals(now, savedBooking.getBookingDate());
		assertEquals(now, savedBooking.getShowTime());
		assertEquals("B2", savedBooking.getSeatId());
		assertEquals("UPDATED", savedBooking.getStatus());
		assertNotNull(savedBooking.getUpdatedAt());
	}

	@Test
	void testUpdateBookingNotFound() {
		Long bookingId = 1L;
		BookingDto updateDto = new BookingDto(bookingId, "new@example.com", 2L, LocalDateTime.now(),
				LocalDateTime.now(), "B2", "REF456", "UPDATED", LocalDateTime.now().minusDays(2),
				LocalDateTime.now().minusDays(1), "New User", "Seat B2");

		when(repo.findById(bookingId)).thenReturn(Optional.empty());

		AppException exception = assertThrows(AppException.class,
				() -> bookingService.updateBooking(bookingId, updateDto));
		assertEquals("Booking not found", exception.getMessage());
		assertEquals(HttpStatus.NOT_FOUND, exception.getStatus());
	}

	@Test
	void testDeleteBooking() {
		Booking booking = new Booking(1L, "user@example.com", 1L, LocalDateTime.now(), LocalDateTime.now(), "1",
				UUID.randomUUID().toString(), "CONFIRMED", LocalDateTime.now(), LocalDateTime.now());
		when(repo.findById(1L)).thenReturn(Optional.of(booking));

		assertDoesNotThrow(() -> bookingService.deleteBooking(1L));
		verify(repo, times(1)).delete(booking);
	}
}
