package com.theater.booking.controller;

import com.theater.booking.dto.BookingDto;
import com.theater.booking.service.contract.IBookingService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import java.time.LocalDateTime;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class BookingControllerTest {

    @Mock
    private IBookingService bookingService;

    @InjectMocks
    private BookingController bookingController;

    private BookingDto bookingDto;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        bookingDto = new BookingDto(1L, "user@example.com", 1L, LocalDateTime.now(),
                LocalDateTime.now(), "1", UUID.randomUUID().toString(), "CONFIRMED",
                LocalDateTime.now(), LocalDateTime.now(), "User Name", "A1");
    }

    @Test
    void testCreateBooking() {
        when(bookingService.createBooking(any(BookingDto.class))).thenReturn(new ResponseEntity<>(new byte[0], HttpStatus.OK));

        ResponseEntity<byte[]> response = bookingController.createBooking(bookingDto);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        verify(bookingService, times(1)).createBooking(any(BookingDto.class));
    }

    @Test
    void testGetBookingById() {
        when(bookingService.getBookingById(1L)).thenReturn(bookingDto);

        ResponseEntity<BookingDto> response = bookingController.getBookingById(1L);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(bookingDto, response.getBody());
        verify(bookingService, times(1)).getBookingById(1L);
    }

    @Test
    void testUpdateBooking() {
        when(bookingService.updateBooking(anyLong(), any(BookingDto.class))).thenReturn(bookingDto);

        ResponseEntity<BookingDto> response = bookingController.updateBooking(1L, bookingDto);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(bookingDto, response.getBody());
        verify(bookingService, times(1)).updateBooking(anyLong(), any(BookingDto.class));
    }
}
