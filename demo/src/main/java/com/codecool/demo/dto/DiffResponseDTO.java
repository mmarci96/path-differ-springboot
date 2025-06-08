package com.codecool.demo.dto;

import java.util.List;

public record DiffResponseDTO(
        String filePathA, String filePathB, List<DiffEntryDTO> differencies) {}
