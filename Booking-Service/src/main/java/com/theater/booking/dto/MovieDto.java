package com.theater.booking.dto;

import java.time.LocalDate;

public record MovieDto(Long id, String title, String description, String genre, LocalDate releaseDate, int duration) {

}
