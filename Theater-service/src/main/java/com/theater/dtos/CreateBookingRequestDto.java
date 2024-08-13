package com.theater.dtos;

public record CreateBookingRequestDto(
        Long userId,
        Long showId,
        int numberOfTickets
) {
}
