package com.theater.dtos;


import java.time.LocalDateTime;

public record BookingDto(
        Long id,
        Long userId,
        Long showId,
        int numberOfTickets,
        LocalDateTime bookingTime,
        double totalPrice,
        String userName
) {
}
