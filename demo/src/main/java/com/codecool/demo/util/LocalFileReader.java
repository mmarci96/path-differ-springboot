package com.codecool.demo.util;

import com.codecool.demo.model.LocalFile;

/**
 * Provides functionality to read and represent file system structures as hierarchical objects.
 * Implementations should recursively traverse directories and create corresponding domain models.
 */
public interface LocalFileReader {
    /**
     * Reads and parses a file system location into a hierarchical representation.
     *
     * @param path Absolute path to a valid file system location (file or directory)
     * @return Root {@link LocalFile} node representing the location
     * @throws LocalFileNotFoundException If the path doesn't exist in the file system
     */
    LocalFile readFileTree(String path);
}
