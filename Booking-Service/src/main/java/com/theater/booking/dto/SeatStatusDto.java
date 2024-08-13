package com.theater.booking.dto;

public record SeatStatusDto(
        Long seatId,
        boolean booked
) {
}
