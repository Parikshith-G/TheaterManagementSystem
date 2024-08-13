package com.theater.booking.dto;

import org.springframework.http.HttpStatus;

public record ErrorDto(String message, HttpStatus status) {
}
