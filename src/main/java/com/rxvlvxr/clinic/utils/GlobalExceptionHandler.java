package com.rxvlvxr.clinic.utils;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.time.LocalDateTime;
import java.time.ZoneId;

// ловим исключения глобально
@ControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler
    public ResponseEntity<ErrorResponse> handleException(DoctorNotFoundException e) {
        return new ResponseEntity<>(new ErrorResponse(e.getMessage(), LocalDateTime.now(ZoneId.systemDefault())), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler
    public ResponseEntity<ErrorResponse> handleException(HttpMessageNotReadableException e) {
        return new ResponseEntity<>(new ErrorResponse(e.getMessage(), LocalDateTime.now(ZoneId.systemDefault())), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler
    private ResponseEntity<ErrorResponse> handleException(PatientNotFoundException e) {
        return new ResponseEntity<>(new ErrorResponse(e.getMessage(), LocalDateTime.now(ZoneId.systemDefault())), HttpStatus.BAD_REQUEST);
    }
}
