package com.codecool.demo.exception;

public class LocalFileNotFoundException extends RuntimeException {
    public LocalFileNotFoundException(String message) {
        super(message);
    }
}
