package com.theater.booking.controller;

import com.theater.booking.dto.BookingDto;

import com.theater.booking.service.contract.IBookingService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/bookings")
//@CrossOrigin("*")
public class BookingController {

	private static final Logger logger = LoggerFactory.getLogger(BookingController.class);
	private IBookingService bookingService;

	@Autowired
	public BookingController(IBookingService bookingService) {
		this.bookingService = bookingService;
	}

	@PostMapping("/book")
	public ResponseEntity<byte[]> createBooking(@RequestBody BookingDto bookingDto) {
		logger.info("Received request to create booking: {}", bookingDto);
		ResponseEntity<byte[]> response = bookingService.createBooking(bookingDto);
		logger.info("Booking created successfully");
		return response;

	}

	@GetMapping("/{id}")
	public ResponseEntity<BookingDto> getBookingById(@PathVariable(name = "id") Long id) {
		logger.info("Received request to get booking by id: {}", id);
		BookingDto bookingDto = bookingService.getBookingById(id);
		logger.info("Booking retrieved: {}", bookingDto);
		return ResponseEntity.ok(bookingDto);
	}

	@PutMapping("/{id}")
	public ResponseEntity<BookingDto> updateBooking(@PathVariable(name = "id") Long id,
			@RequestBody BookingDto bookingDto) {
		logger.info("Received request to update booking with id: {}", id);
		BookingDto updatedBooking = bookingService.updateBooking(id, bookingDto);
		logger.info("Booking updated successfully: {}", updatedBooking);
		return ResponseEntity.ok(updatedBooking);
	}

}
