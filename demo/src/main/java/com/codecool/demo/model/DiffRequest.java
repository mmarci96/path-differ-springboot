package com.codecool.demo.model;

import com.codecool.demo.dto.DiffResponseDTO;
import com.codecool.demo.dto.HistoryEntryDTO;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;

import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

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

    @OneToMany(mappedBy = "diffRequest", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<DiffEntry> differences = new HashSet<>();

    public DiffRequest(String username, LocalFile localFileA, LocalFile localFileB) {
        this.username = username;
        this.localFileA = localFileA;
        this.localFileB = localFileB;
        this.createdAt = LocalDateTime.now();
    }

    public void addDifference(DiffEntry entry) {
        entry.setDiffRequest(this);
        differences.add(entry);
    }

    public HistoryEntryDTO toHistoryDTO() {
        var diffEntryDTOs = differences.stream().map(DiffEntry::toDiffEntryDTO).toList();

        return new HistoryEntryDTO(
                username,
                new DiffResponseDTO(localFileA.getPath(), localFileB.getPath(), diffEntryDTOs),
                createdAt);
    }
}
