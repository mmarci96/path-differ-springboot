package com.codecool.demo.model;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

@Entity
@Table(name = "directories")
@NoArgsConstructor
@Getter
@Setter
public class Directory extends LocalFile {

    @OneToMany(mappedBy = "directory", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<LocalFile> localFiles = new HashSet<>();

    public void addLocalFile(LocalFile localFile) {
        localFile.setDirectory(this);
        localFiles.add(localFile);
    }

    public Map<String, LocalFile> getAllNestedFilesWithRelativePaths(String prefix) {
        Map<String, LocalFile> result = new HashMap<>();
        for (LocalFile file : localFiles) {
            Path relPath = Paths.get(prefix).resolve(file.getName());
            if (file instanceof Directory dir) {
                result.putAll(dir.getAllNestedFilesWithRelativePaths(relPath.toString()));
            } else {
                result.put(relPath.toString(), file);
            }
        }
        return result;
    }
}
