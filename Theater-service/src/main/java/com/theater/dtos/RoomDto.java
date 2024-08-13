package com.theater.dtos;

import java.util.List;

public record RoomDto(
        Long id,
        Long theaterId,
        String name,
        int capacity,
        int availableSeats,
        List<Long> showIds,
        List<SeatStatusDto> seatStatuses

) {
}
