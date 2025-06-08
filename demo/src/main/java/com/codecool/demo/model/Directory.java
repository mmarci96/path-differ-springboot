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
import java.util.stream.Collectors;

@Entity
@Table(name = "directories")
@NoArgsConstructor
@Getter
@Setter
public class Directory extends LocalFile {

    @OneToMany(mappedBy = "directory", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<LocalFile> localFiles = new HashSet<>();

    public void addLocalFile(LocalFile localFile) {
        localFiles.add(localFile);
    }

    public Set<Directory> getSubdirectories() {
        return localFiles.stream()
                .filter(file -> file instanceof Directory)
                .map(file -> (Directory) file)
                .collect(Collectors.toSet());
    }

  
public Map<String, LocalFile> getAllNestedFilesWithRelativePaths(String prefix) {
    Map<String, LocalFile> result = new HashMap<>();
    for (LocalFile file : localFiles) {
        String relPath = prefix + "/" + file.getName();
        if (file instanceof Directory dir) {
            result.putAll(dir.getAllNestedFilesWithRelativePaths(relPath));
        } else {
            result.put(relPath, file);
        }
    }
    return result;
}
}
