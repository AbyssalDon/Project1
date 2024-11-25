package com.example.demo.Exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@ControllerAdvice
public class GlobalExceptionHandler {
    Map<String, Object> errorDetails = new HashMap<>();

    @ExceptionHandler({CprNotValidException.class})
    public ResponseEntity<Object> handleCprNotValidException(CprNotValidException exception){
        errorDetails.put("message", exception.getMessage());
        errorDetails.put("code", HttpStatus.FORBIDDEN.value());
        errorDetails.put("details", "CPR must be integer and it has to be of size 9 digits.");

        return ResponseEntity
                .status(HttpStatus.FORBIDDEN)
                .body(errorDetails);
    }

    @ExceptionHandler({UserNotFoundException.class})
    public ResponseEntity<Object> handleUserNotFoundException(UserNotFoundException exception){
        errorDetails.put("message", exception.getMessage());
        errorDetails.put("code", HttpStatus.NO_CONTENT.value());
        errorDetails.put("details", "The user must exist in the database.");

        return ResponseEntity
                .status(HttpStatus.NO_CONTENT)
                .body(errorDetails);
    }
}
