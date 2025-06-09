package com.codecool.demo.controller;

import com.codecool.demo.exception.LocalFileNotFoundException;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;
import java.util.Map;

/**
 * Global exception handler for REST controllers. Provides centralized error handling for specific
 * exception types.
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

    /**
     * Handles file system path resolution failures.
     *
     * @param ex The caught exception instance
     * @return HTTP 404 response with error message:
     *     <pre>{"error": "No file at: /invalid/path"}</pre>
     */
    @ExceptionHandler(LocalFileNotFoundException.class)
    public ResponseEntity<?> handleLocalFileNotFound(LocalFileNotFoundException ex) {
        Map<String, String> error = new HashMap<>();
        error.put("error", ex.getMessage());
        return ResponseEntity.status(404).body(error);
    }

    /**
     * Handles general runtime exception.
     *
     * @param ex The caught exception instance
     * @return HTTP BadRequest response with error message:
     *     <pre>{"error": "Something went wrong: message"}</pre>
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<?> handleGeneralException(Exception ex) {
        Map<String, String> error = new HashMap<>();
        error.put("error", "Something went wrong: " + ex.getMessage());
        return ResponseEntity.badRequest().body(error);
    }
}
