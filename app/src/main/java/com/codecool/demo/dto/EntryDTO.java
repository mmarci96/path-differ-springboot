package com.codecool.demo.dto;

/**
 * Represents a single file system entry in comparison results.
 *
 * @param name Relative path/filename
 * @param size File size in bytes
 */
public record EntryDTO(String name, Long size) {}
