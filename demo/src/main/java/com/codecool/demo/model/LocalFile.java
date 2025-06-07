package com.codecool.demo.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

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

    private String name;
    private String path;
    private long size;

    // Add reference to parent directory
    @ManyToOne
    @JoinColumn(name = "directory_id")
    private Directory directory;
}

