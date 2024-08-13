package com.theater.exception;


import lombok.Data;
import lombok.EqualsAndHashCode;

import org.springframework.http.HttpStatus;

@Data
@EqualsAndHashCode(callSuper=false)
public class AppException extends RuntimeException {
    private HttpStatus status;
    private String message;
    public AppException(String message,HttpStatus status) {
        super(message+" "+status);
        this.status=status;
        this.message=message;
    }
}
