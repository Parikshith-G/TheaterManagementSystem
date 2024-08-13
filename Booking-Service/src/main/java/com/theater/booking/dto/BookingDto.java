package com.theater.booking.dto;

import java.time.LocalDateTime;

public record BookingDto(
        Long id,
        String userMail,
        Long showId,
        LocalDateTime bookingDate,
        LocalDateTime showTime,
        String seatId,
        String bookingReference,
        String status,
        LocalDateTime createdAt,
        LocalDateTime updatedAt,
        String userName,
        String seatName
     

     
) {
}
