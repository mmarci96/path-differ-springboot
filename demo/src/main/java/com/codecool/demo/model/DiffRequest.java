package com.codecool.demo.model;

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
}
