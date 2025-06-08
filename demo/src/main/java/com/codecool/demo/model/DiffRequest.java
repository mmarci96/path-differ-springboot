package com.codecool.demo.model;

import com.codecool.demo.dto.DiffResponseDTO;
import com.codecool.demo.dto.EntryDTO;
import com.codecool.demo.dto.HistoryEntryDTO;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;

import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Entity
@Table(name = "diff_request")
@Getter
@NoArgsConstructor
public class DiffRequest {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String username;

    @OneToOne
    @JoinColumn(name = "local_file_a_id", nullable = false)
    private LocalFile localFileA;

    @OneToOne
    @JoinColumn(name = "local_file_b_id", nullable = false)
    private LocalFile localFileB;

    private LocalDateTime createdAt;

    public DiffRequest(String username, LocalFile localFileA, LocalFile localFileB) {
        this.username = username;
        this.localFileA = localFileA;
        this.localFileB = localFileB;
        this.createdAt = LocalDateTime.now();
    }

    public DiffResponseDTO getResponseDTO() {
        Set<EntryDTO> sameFiles = new HashSet<>();

        Set<EntryDTO> onlyPathA = new HashSet<>();
        Set<EntryDTO> onlyPathB = new HashSet<>();

        if (localFileA instanceof Directory dirA && localFileB instanceof Directory dirB) {
            sameFiles.addAll(
                    compareDirs(dirA, dirB).entrySet().stream()
                            .map(e -> new EntryDTO(e.getKey(), e.getValue().getSize()))
                            .collect(Collectors.toSet()));
        } else {
            var matchingFile = compareFiles(localFileA, localFileB);
            if (matchingFile != null) {
                sameFiles.add(new EntryDTO(matchingFile.name(), matchingFile.size()));
            } else {
                onlyPathA.add(new EntryDTO(localFileA.getName(), localFileA.getSize()));
                onlyPathB.add(new EntryDTO(localFileB.getName(), localFileB.getSize()));
            }
        }

        return new DiffResponseDTO(
                localFileA.getPath(), localFileB.getPath(), onlyPathA, onlyPathB, sameFiles);
    }

    private EntryDTO compareFiles(LocalFile fileA, LocalFile fileB) {
        if (!fileA.getName().equals(fileB.getName()) || fileB.getSize() != fileA.getSize()) {
            return null;
        }
        return new EntryDTO(fileB.getName(), fileA.getSize());
    }

    private Map<String, LocalFile> compareDirs(Directory dirA, Directory dirB) {
        Map<String, LocalFile> sameFiles = new HashMap<>();

        Map<String, LocalFile> mapA =
                dirA.getLocalFiles().stream().collect(Collectors.toMap(LocalFile::getName, f -> f));

        Map<String, LocalFile> mapB =
                dirB.getLocalFiles().stream().collect(Collectors.toMap(LocalFile::getName, f -> f));

        for (String name : mapA.keySet()) {
            LocalFile fileA = mapA.get(name);
            LocalFile fileB = mapB.get(name);

            if (fileB == null) continue;

            if (fileA instanceof Directory subDirA && fileB instanceof Directory subDirB) {
                // Recurse into subdirectories
                sameFiles.putAll(compareDirs(subDirA, subDirB));
            } else if (!(fileA instanceof Directory) && !(fileB instanceof Directory)) {
                // Both are files
                if (fileA.getSize() == fileB.getSize()) {
                    sameFiles.put(fileA.getPath(), fileA); // optionally use fileA.getName()
                }
            }
        }

        return sameFiles;
    }

    public HistoryEntryDTO toHistoryDTO() {
        var diffResp = getResponseDTO();
        return new HistoryEntryDTO(username, diffResp, createdAt);
    }
}
