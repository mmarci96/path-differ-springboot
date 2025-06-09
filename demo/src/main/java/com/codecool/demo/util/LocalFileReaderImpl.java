package com.codecool.demo.util;

import com.codecool.demo.exception.LocalFileNotFoundException;
import com.codecool.demo.model.Directory;
import com.codecool.demo.model.LocalFile;
import com.codecool.demo.repository.DirectoryRepository;
import com.codecool.demo.repository.LocalFileRepository;

import org.springframework.stereotype.Component;

import java.io.File;

/**
 * Recursive file system reader that persists file/directory structures to the database.
 *
 * <p>Key features: - Converts physical file system objects to domain entities - Maintains
 * parent-child relationships for directories - Calculates directory sizes based on nested content -
 * Automatically persists entities through JPA repositories
 *
 * <p>Behavior: - For files: Creates LocalFile entity with exact size - For directories: Recursively
 * processes children, calculates total size, and maintains hierarchical relationships
 */
@Component
public class LocalFileReaderImpl implements LocalFileReader {

    private final LocalFileRepository localFileRepository;
    private final DirectoryRepository directoryRepository;

    /**
     * Constructs the file reader with necessary persistence repositories.
     *
     * @param localFileRepository Repository for file entities
     * @param directoryRepository Repository for directory entities
     */
    public LocalFileReaderImpl(
            LocalFileRepository localFileRepository, DirectoryRepository directoryRepository) {
        this.localFileRepository = localFileRepository;
        this.directoryRepository = directoryRepository;
    }

    /**
     * Entry point for reading a file system location.
     *
     * @param path Absolute path to a valid file system entry
     * @return Root entity representing the location
     * @throws LocalFileNotFoundException if path doesn't exist
     */
    @Override
    public LocalFile readFileTree(String path) {
        File file = new File(path);
        if (!file.exists()) {
            throw new LocalFileNotFoundException("No file at: " + path);
        }
        return process(file);
    }

    /**
     * Recursively processes a file system entry and its children.
     *
     * <p>Directory processing: 1. Creates directory entity 2. Processes all children recursively 3.
     * Calculates total size as sum of child sizes 4. Persists directory with complete hierarchy
     *
     * <p>File processing: - Creates file entity with direct size
     *
     * @param file Physical file system entry to process
     * @return Domain entity representing the file/directory
     */
    private LocalFile process(File file) {
        if (file.isDirectory()) {
            Directory directory = new Directory();
            directory.setName(file.getName());
            directory.setPath(file.getPath());
            directoryRepository.save(directory);

            long totalSize = 0;
            File[] children = file.listFiles();

            if (children != null) {
                for (File child : children) {
                    LocalFile localChild = process(child);
                    localChild.setDirectory(directory);
                    directory.addLocalFile(localChild);
                    totalSize += localChild.getBytes();
                }
            }

            directory.setBytes(totalSize);
            directoryRepository.save(directory);
        } else {
            LocalFile localFile = new LocalFile();
            localFile.setName(file.getName());
            localFile.setPath(file.getPath());
            localFile.setBytes(file.length());
            localFileRepository.save(localFile);
            return localFile;
        }
    }
}
