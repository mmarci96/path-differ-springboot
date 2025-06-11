package com.codecool.demo.dto;

import java.util.Set;

/**
 * Comparison result between two file system locations.
 *
 * @param filePathA Path of first location
 * @param filePathB Path of second location
 * @param onlyPathA Files unique to first location
 * @param onlyPathB Files unique to second location
 * @param shared Files present in both with matching sizes
 */
public record DiffResponseDTO(
        String filePathA,
        String filePathB,
        Set<FileEntryDTO> onlyPathA,
        Set<FileEntryDTO> onlyPathB,
        Set<FileEntryDTO> shared) {}
