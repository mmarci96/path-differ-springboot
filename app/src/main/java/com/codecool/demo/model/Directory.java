package com.codecool.demo.model;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * Specialized entity representing directories. Maintains hierarchical relationships with contained
 * files/subdirectories.
 */
@Entity
@Table(name = "directories")
@NoArgsConstructor
@Getter
@Setter
public class Directory extends LocalFile {
    /** Contained files and subdirectories */
    @OneToMany(mappedBy = "directory", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<LocalFile> localFiles = new HashSet<>();

    /**
     * Adds a file/subdirectory to this directory. Establishes bidirectional relationship.
     *
     * @param localFile File or directory to add
     */
    public void addLocalFile(LocalFile localFile) {
        localFile.setDirectory(this);
        localFiles.add(localFile);
    }

    /**
     * Recursively collects all nested files with relative paths.
     *
     * @param prefix Current relative path prefix
     * @return Map of relative paths to file entities
     */
    public Map<String, LocalFile> getAllNestedFilesWithRelativePaths(String prefix) {
        Map<String, LocalFile> result = new HashMap<>();
        for (LocalFile file : localFiles) {
            String relativePath = prefix.isEmpty() ? file.getName() : prefix + "/" + file.getName();

            if (file instanceof Directory dir) {
                result.putAll(dir.getAllNestedFilesWithRelativePaths(relativePath));
            } else {
                result.put(relativePath, file);
            }
        }
        return result;
    }
}
