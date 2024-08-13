package com.login.exception;

import java.util.HashMap;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@ControllerAdvice
public class GlobalExceptionHandler {

	private static final Logger logger = LoggerFactory.getLogger(GlobalExceptionHandler.class);

	@ExceptionHandler(UserNotFoundException.class)
	public ResponseEntity<ErrorResponse> handleUserNotFoundException(UserNotFoundException e, WebRequest request) {
		logger.error("UserNotFoundException: {}", e.getMessage());
		ErrorResponse error = new ErrorResponse(HttpStatus.NOT_FOUND, e.getMessage());
		return new ResponseEntity<>(error, HttpStatus.NOT_FOUND);
	}

	@ExceptionHandler(UserAlreadyPresentException.class)
	public ResponseEntity<ErrorResponse> handleUserAlreadyPresentException(UserAlreadyPresentException e,
			WebRequest request) {
		logger.error("UserAlreadyPresentException: {}", e.getMessage());
		ErrorResponse error = new ErrorResponse(HttpStatus.CONFLICT, e.getMessage());
		return new ResponseEntity<>(error, HttpStatus.CONFLICT);
	}

	@ExceptionHandler(InvalidCredentialException.class)
	public ResponseEntity<ErrorResponse> handleInvalidCredentialException(InvalidCredentialException e,
			WebRequest request) {
		logger.error("InvalidCredentialException: {}", e.getMessage());
		ErrorResponse error = new ErrorResponse(HttpStatus.BAD_REQUEST, e.getMessage());
		return new ResponseEntity<>(error, HttpStatus.BAD_REQUEST);
	}

	@ExceptionHandler(MethodArgumentNotValidException.class)
	public ResponseEntity<Map<String, String>> handleIllegalFieldValueException(MethodArgumentNotValidException ex) {
		logger.error("MethodArgumentNotValidException: {}", ex.getMessage());

		Map<String, String> errorMap = new HashMap<>();

		ex.getBindingResult().getAllErrors().forEach(error -> {
			String fieldName = ((FieldError) error).getField();
			String msg = error.getDefaultMessage();

			errorMap.put(fieldName, msg);

		});

		return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorMap);
	}

	@ExceptionHandler(Exception.class)
	public ResponseEntity<ErrorResponse> handleGlobalException(Exception e, WebRequest request) {
		logger.error("Exception: {}", e.getMessage(), e);
		ErrorResponse error = new ErrorResponse(HttpStatus.BAD_REQUEST, e.getMessage());
		return new ResponseEntity<>(error, HttpStatus.BAD_REQUEST);
	}

}
