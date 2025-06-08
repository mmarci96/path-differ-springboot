package com.codecool.demo.exception;

/** Exception extending RuntimeException */
public class LocalFileNotFoundException extends RuntimeException {
    /**
     * Custom Exception to handle LocalFile not found on path.
     *
     * @param message message to send about not found file
     */
    public LocalFileNotFoundException(String message) {
        super(message);
    }
}
