package com.codecool.demo.model;

import jakarta.persistence.*;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

@Entity
@Table(name = "directories")
@NoArgsConstructor
@Getter
@Setter
public class Directory extends LocalFile {

    @OneToMany(mappedBy = "directory", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<LocalFile> localFiles = new HashSet<>();

    public Set<Directory> getSubdirectories() {
        return localFiles.stream()
                .filter(file -> file instanceof Directory)
                .map(file -> (Directory) file)
                .collect(Collectors.toSet());
    }
}
