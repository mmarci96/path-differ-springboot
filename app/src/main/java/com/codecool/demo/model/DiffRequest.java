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

/**
 * Represents a file/directory comparison request.
 * Stores metadata about comparison operations for historical tracking.
 */
@Entity
@Table(name = "diff_request")
@Getter
@NoArgsConstructor
public class DiffRequest {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** Username initiating the comparison */
    private String username;

    /** First file/directory in comparison */
    @OneToOne
    @JoinColumn(name = "local_file_a_id", nullable = false)
    private LocalFile localFileA;

    /** Second file/directory in comparison */
    @OneToOne
    @JoinColumn(name = "local_file_b_id", nullable = false)
    private LocalFile localFileB;

    /** Timestamp of request creation */
    private LocalDateTime createdAt;

    /**
     * Creates a new comparison request.
     * 
     * @param username   Initiating user
     * @param localFileA First comparison target
     * @param localFileB Second comparison target
     */
    public DiffRequest(String username, LocalFile localFileA, LocalFile localFileB) {
        this.username = username;
        this.localFileA = localFileA;
        this.localFileB = localFileB;
        this.createdAt = LocalDateTime.now();
    }
}
