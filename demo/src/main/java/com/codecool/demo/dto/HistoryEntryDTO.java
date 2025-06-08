package com.codecool.demo.dto;

import java.time.LocalDateTime;

public record HistoryEntryDTO(String username, DiffResponseDTO results, LocalDateTime createdAt) {}
