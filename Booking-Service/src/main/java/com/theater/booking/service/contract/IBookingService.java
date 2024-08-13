package com.theater.booking.service.contract;


import org.springframework.http.ResponseEntity;

import com.theater.booking.dto.BookingDto;

public interface IBookingService {
	ResponseEntity<byte[]> createBooking(BookingDto bookingDto);

    BookingDto getBookingById(Long id);

    BookingDto updateBooking(Long id, BookingDto bookingDto);

    void deleteBooking(Long id);
}
