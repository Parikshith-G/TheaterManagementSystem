package com.theater.dtos;

import org.springframework.http.HttpStatus;

public record ErrorDto(String message, HttpStatus status) {
}
