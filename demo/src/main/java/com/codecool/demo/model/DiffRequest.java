package com.codecool.demo.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;

import lombok.Getter;

import java.time.LocalDateTime;
import java.util.Set;

@Entity
@Table(name = "diff_request")
@Getter
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

    public boolean areFilesStructurallyEqual(LocalFile fileA, LocalFile fileB) {
        if (fileA == null || fileB == null) return false;

        if (!fileA.getName().equals(fileB.getName())) return false;
        if (fileA.getClass() != fileB.getClass()) return false;

        if (!(fileA instanceof Directory)) return true;

        Directory dirA = (Directory) fileA;
        Directory dirB = (Directory) fileB;

        Set<LocalFile> contentsA = dirA.getLocalFiles();
        Set<LocalFile> contentsB = dirB.getLocalFiles();

        if (contentsA.size() != contentsB.size()) return false;

        for (LocalFile childA : contentsA) {
            boolean matchFound =
                    contentsB.stream()
                            .anyMatch(childB -> areFilesStructurallyEqual(childA, childB));
            if (!matchFound) return false;
        }

        return true;
    }
}
