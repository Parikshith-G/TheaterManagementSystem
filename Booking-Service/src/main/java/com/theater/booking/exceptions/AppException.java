package com.theater.booking.exceptions;

import lombok.Data;
import lombok.EqualsAndHashCode;

import org.springframework.http.HttpStatus;

@Data
@EqualsAndHashCode(callSuper = false)
public class AppException extends RuntimeException{
    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private final String message;
    private final HttpStatus status;
    public AppException(String message, HttpStatus httpStatus) {
        super(message+" "+httpStatus);
        this.message=message;
        this.status=httpStatus;
    }
}
