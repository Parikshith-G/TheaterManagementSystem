package com.login.exceptions;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.context.request.WebRequest;

import com.login.exception.ErrorResponse;
import com.login.exception.GlobalExceptionHandler;
import com.login.exception.InvalidCredentialException;
import com.login.exception.UserAlreadyPresentException;
import com.login.exception.UserNotFoundException;

import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;
import org.springframework.validation.FieldError;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class GlobalExceptionHandlerTest {

	private GlobalExceptionHandler globalExceptionHandler;

	@BeforeEach
	void setUp() {
		globalExceptionHandler = new GlobalExceptionHandler();
	}

	@Test
	void testHandleUserNotFoundException() {
		UserNotFoundException ex = new UserNotFoundException("User not found");
		ResponseEntity<ErrorResponse> response = globalExceptionHandler.handleUserNotFoundException(ex,
				mock(WebRequest.class));

		assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
		assertThat(response.getBody().getMessage()).isEqualTo("User not found");
	}

	@Test
	void testHandleUserAlreadyPresentException() {
		UserAlreadyPresentException ex = new UserAlreadyPresentException("User already exists");
		ResponseEntity<ErrorResponse> response = globalExceptionHandler.handleUserAlreadyPresentException(ex,
				mock(WebRequest.class));

		assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CONFLICT);
		assertThat(response.getBody().getMessage()).isEqualTo("User already exists");
	}

	@Test
	void testHandleInvalidCredentialException() {
		InvalidCredentialException ex = new InvalidCredentialException("Invalid credentials");
		ResponseEntity<ErrorResponse> response = globalExceptionHandler.handleInvalidCredentialException(ex,
				mock(WebRequest.class));

		assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
		assertThat(response.getBody().getMessage()).isEqualTo("Invalid credentials");
	}

	@Test
	void testHandleMethodArgumentNotValidException() {
		// Mock BindingResult and FieldError
		BindingResult bindingResult = mock(BindingResult.class);
		FieldError fieldError = new FieldError("objectName", "field", "error message");
		List<ObjectError> errors = new ArrayList<>();
		errors.add(fieldError);
		when(bindingResult.getAllErrors()).thenReturn(errors);

		// Mock MethodArgumentNotValidException
		MethodArgumentNotValidException ex = mock(MethodArgumentNotValidException.class);
		when(ex.getBindingResult()).thenReturn(bindingResult);

		// Call the exception handler
		ResponseEntity<Map<String, String>> response = globalExceptionHandler.handleIllegalFieldValueException(ex);

		// Verify the response
		assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
		Map<String, String> errorMap = response.getBody();
		assertThat(errorMap).containsEntry("field", "error message");
	}

	@Test
	void testHandleGlobalException() {
		Exception ex = new Exception("General error");
		ResponseEntity<ErrorResponse> response = globalExceptionHandler.handleGlobalException(ex,
				mock(WebRequest.class));

		assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
		assertThat(response.getBody().getMessage()).isEqualTo("General error");
	}
}
