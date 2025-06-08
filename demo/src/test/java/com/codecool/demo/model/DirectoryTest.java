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
        root.setPath("/root");

        Directory subDir = new Directory();
        subDir.setName("sub");
        subDir.setPath("/root/sub");

        LocalFile file1 = new LocalFile();
        file1.setName("file1.txt");
        file1.setSize(100);
        file1.setPath("/root/file1.txt");

        LocalFile file2 = new LocalFile();
        file2.setName("file2.txt");
        file2.setSize(200);
        file2.setPath("/root/sub/file2.txt");

        subDir.addLocalFile(file2);
        root.addLocalFile(file1);
        root.addLocalFile(subDir);

        Map<String, LocalFile> nested = root.getAllNestedFilesWithRelativePaths("");
        assertEquals(2, nested.size());
        assertTrue(nested.containsKey("file1.txt"));
        assertTrue(nested.containsKey("sub/file2.txt"));
        assertTrue(nested.values().contains(file1));
        assertTrue(nested.values().contains(file2));
    }

    @Test
    void test_GetAllNestedFiles_WhenNestingMultipleDir() {
        Directory root = new Directory();
        root.setPath("/root");
        root.setName("root");
        root.setSize(40);

        Directory children = new Directory();
        children.setSize(20);
        children.setName("child");
        children.setPath("/root/child");

        Directory nestedChild = new Directory();
        nestedChild.setSize(10);
        nestedChild.setName("nested-child");
        nestedChild.setPath("/root/child/nested-child");

        LocalFile file = new LocalFile();
        file.setName("file.txt");
        file.setPath("/root/child/nested-child/file.txt");
        file.setSize(10);

        nestedChild.addLocalFile(file);
        children.addLocalFile(nestedChild);
        root.addLocalFile(children);

        var files = root.getAllNestedFilesWithRelativePaths("");
        assertEquals(1, files.size());
        assertTrue(files.containsKey("child/nested-child/file.txt"));
    }
}
