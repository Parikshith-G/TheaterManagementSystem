package com.theater.dtos;
import java.time.LocalDateTime;

public record ShowDto(
        Long id,
        Long movieId,
        Long roomId,
        LocalDateTime startTime,
        LocalDateTime endTime,
        Double price
) {
}
