package com.theater.dtos;



public record SeatStatusDto(
        Long seatId,
        boolean booked
) {
}
