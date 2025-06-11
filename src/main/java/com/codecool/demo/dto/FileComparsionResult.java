package com.codecool.demo.dto;

import java.util.Set;

public record FileComparsionResult(
        Set<FileEntryDTO> sharedFiles, Set<FileEntryDTO> onlyInMap, Set<FileEntryDTO> onlyInSet) {}
