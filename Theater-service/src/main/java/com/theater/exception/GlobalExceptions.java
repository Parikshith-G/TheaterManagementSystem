package com.theater.exception;


import com.theater.dtos.ErrorDto;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class GlobalExceptions {

    @ExceptionHandler(AppException.class)
    public ResponseEntity<ErrorDto> handleException(AppException ex){
        ErrorDto dto=new ErrorDto(ex.getMessage(),ex.getStatus());
        return new ResponseEntity<ErrorDto>(dto,ex.getStatus());
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorDto> handleError(Exception ex){
        ErrorDto dto=new ErrorDto(ex.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        return new ResponseEntity<ErrorDto>(dto,HttpStatus.INTERNAL_SERVER_ERROR);
    }
}

