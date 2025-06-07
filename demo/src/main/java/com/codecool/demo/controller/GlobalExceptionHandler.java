package com.codecool.demo.controller;

import com.codecool.demo.exception.FileNotFoundException;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.util.HashMap;
import java.util.Map;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(FileNotFoundException.class)
    public ResponseEntity<?> handleFileNotFoundException(
            FileNotFoundException fileNotFoundException) {
        Map<String, String> error = new HashMap<>();
        error.put("error", fileNotFoundException.getMessage());
        return ResponseEntity.status(404).body(error);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<?> handleGeneralException(Exception ex) {
        Map<String, String> error = new HashMap<>();
        error.put("error", "Something went wrong" + ex.getMessage());
        return ResponseEntity.badRequest().body(error);
    }
}
