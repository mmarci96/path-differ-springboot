package com.codecool.demo.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Inheritance;
import jakarta.persistence.InheritanceType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Base entity representing a file system entry. Uses joined table inheritance strategy for
 * directory specialization.
 */
@Entity
@Inheritance(strategy = InheritanceType.JOINED)
@Table(name = "local_files")
@NoArgsConstructor
@Getter
@Setter
public class LocalFile {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** Name of file/directory */
    private String name;

    /** Absolute path in filesystem */
    private String path;

    /** Size in bytes (for directories: calculated total size) */
    private long size;

    /** Parent directory (null for root entries) */
    @ManyToOne
    @JoinColumn(name = "directory_id")
    private Directory directory;
}
