package com.codecool.demo.dto;

import java.util.Set;

public record DiffResponseDTO(
        String filePathA,
        String filePathB,
        Set<EntryDTO> onlyPathA,
        Set<EntryDTO> onlyPathB,
        Set<EntryDTO> shared) {}
