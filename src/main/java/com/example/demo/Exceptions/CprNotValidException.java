package com.example.demo.Exceptions;

public class CprNotValidException extends RuntimeException{
    public CprNotValidException(String message) {
        super(message);
    }
}
