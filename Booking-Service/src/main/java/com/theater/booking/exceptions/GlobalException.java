package com.theater.booking.exceptions;

import com.theater.booking.dto.ErrorDto;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@ControllerAdvice
public class GlobalException {
	private static final Logger logger = LoggerFactory.getLogger(GlobalException.class);

	@ExceptionHandler(AppException.class)
	public ResponseEntity<ErrorDto> handleException(AppException ex) {
		logger.error("Application exception occurred: {}", ex.getMessage(), ex);
		ErrorDto dto = new ErrorDto(ex.getMessage(), ex.getStatus());
		return new ResponseEntity<>(dto, ex.getStatus());
	}

	@ExceptionHandler(Exception.class)
	public ResponseEntity<ErrorDto> handleError(Exception ex) {
		logger.error("Unexpected exception occurred: {}", ex.getMessage(), ex);
		ErrorDto dto = new ErrorDto(ex.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
		return new ResponseEntity<>(dto, HttpStatus.INTERNAL_SERVER_ERROR);
	}
}
