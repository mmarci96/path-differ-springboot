package com.codecool.demo.model;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

import java.util.Map;

public class DirectoryTest {

    @Test
    void test_addValidLocalFile_toDir() {
        Directory dir = new Directory();
        LocalFile file = new LocalFile();

        file.setName("file.txt");
        file.setSize(123);
        dir.addLocalFile(file);

        assertTrue(dir.getLocalFiles().contains(file));
    }

    @Test
    void test_getAllNestedFilesWithRelativePaths_whenEmpty() {
        Directory root = new Directory();
        root.setName("root");
        Map<String, LocalFile> nested = root.getAllNestedFilesWithRelativePaths("");
        assertEquals(0, nested.size());
    }

    @Test
    void test_GetAllNestedFilesWithRelativePaths_whenHasChildren() {
        Directory root = new Directory();
        root.setName("root");

        Directory subDir = new Directory();
        subDir.setName("sub");

        LocalFile file1 = new LocalFile();
        file1.setName("file1.txt");
        file1.setSize(100);

        LocalFile file2 = new LocalFile();
        file2.setName("file2.txt");
        file2.setSize(200);

        subDir.addLocalFile(file2);
        root.addLocalFile(file1);
        root.addLocalFile(subDir);

        Map<String, LocalFile> nested = root.getAllNestedFilesWithRelativePaths("");

        assertEquals(2, nested.size());
        assertTrue(nested.containsKey("file1.txt"));
        assertTrue(nested.containsKey("sub/file2.txt"));
    }
}
