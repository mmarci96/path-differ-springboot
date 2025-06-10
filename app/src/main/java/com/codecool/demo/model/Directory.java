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

    private Long totalChildCount;

    /**
     * Adds a file/subdirectory to this directory. Establishes bidirectional relationship. If its a
     * directory increment the totalChildCount to keep up with total nested files.
     *
     * @param localFile File or directory to add
     */
    public void addLocalFile(LocalFile localFile) {
        if (localFile instanceof Directory dir) {
            totalChildCount += dir.getTotalChildCount();
        }
        localFile.setDirectory(this);
        localFiles.add(localFile);
    }

    /**
     * Recursively collects all nested files with relative paths to a HashMap.
     *
     * @param prefix Current relative path prefix
     * @return Map of relative paths to file entities
     */
    public Map<String, LocalFile> getMapOfNestedFilesWithRelativePaths(String prefix) {
        Map<String, LocalFile> result = new HashMap<>();
        for (LocalFile file : localFiles) {
            String relativePath = prefix.isEmpty() ? file.getName() : prefix + "/" + file.getName();

            if (file instanceof Directory dir) {
                result.putAll(dir.getMapOfNestedFilesWithRelativePaths(relativePath));
            } else {
                result.put(relativePath, file);
            }
        }
        return result;
    }
    /**
     * Recursively collects all nested files with relative paths to a HashSet.
     *
     * @param prefix Current relative path prefix
     * @return Set of relative paths to file entities
     */
    public Set<LocalFile> getSetOfNestedFilesWithRelativePaths(String prefix) {
        Set<LocalFile> result = new HashSet<>();
        for (LocalFile file : localFiles) {
            String relativePath = prefix.isEmpty() ? file.getName() : prefix + "/" + file.getName();

            if (file instanceof Directory dir) {
                result.addAll(dir.getSetOfNestedFilesWithRelativePaths(relativePath));
            } else {
                result.add(file);
            }
        }

        return result;
    }
}
