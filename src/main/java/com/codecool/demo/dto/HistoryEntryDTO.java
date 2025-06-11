package com.codecool.demo.dto;

import java.time.LocalDateTime;

/**
 * Historical record of a comparison operation.
 *
 * @param username User who initiated comparison
 * @param results Snapshot of comparison outcome
 * @param createdAt Timestamp of request
 */
public record HistoryEntryDTO(String username, DiffResponseDTO results, LocalDateTime createdAt) {}
