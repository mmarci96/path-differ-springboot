package com.codecool.demo.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import com.codecool.demo.model.Directory;
import com.codecool.demo.model.LocalFile;
import com.codecool.demo.repository.DiffRequestRepository;
import com.codecool.demo.util.LocalFileReader;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Map;

@ExtendWith(MockitoExtension.class)
public class LocalFileServiceTest {

    @Mock private LocalFileReader fileReader;

    @Mock private DiffRequestRepository diffRequestRepository;

    @InjectMocks private LocalFileService localFileService;

    // private Directory mockDirectory(String name, long size, Map<String, LocalFile> nestedFiles) {
    //     Directory dir = mock(Directory.class);
    //     when(dir.getName()).thenReturn(name);
    //     when(dir.getSize()).thenReturn(size);
    //     when(dir.getAllNestedFilesWithRelativePaths("")).thenReturn(nestedFiles);
    //     return dir;
    // }

    @BeforeEach
    void setUp() {
        // Any shared setup if needed
    }

    @Test
    void compareFiles_shouldClassifyDifferencesCorrectly() {
        // Arrange
        var file1 = new LocalFile();
        file1.setName("file1.txt");
        file1.setPath("/path/dirA/file1.txt");
        file1.setBytes(100);

        var file2 = new LocalFile();
        file2.setName("file2.txt");
        file2.setPath("/path/dirA/file2.txt");
        file2.setBytes(110);

        var file3 = new LocalFile();
        file3.setName("file3.txt");
        file3.setPath("/path/dirB/file3.txt");
        file3.setBytes(160);

        var file4 = new LocalFile();
        file4.setName("file4.txt");
        file4.setPath("/path/dirB/file4.txt");
        file4.setBytes(100);

        var dirA = mock(Directory.class);
        when(dirA.getAllNestedFilesWithRelativePaths(""))
                .thenReturn(
                        Map.of(
                                "file1.txt", file1,
                                "file2.txt", file2));

        var dirB = mock(Directory.class);
        when(dirB.getAllNestedFilesWithRelativePaths(""))
                .thenReturn(
                        Map.of(
                                "file1.txt", file1,
                                "file3.txt", file3));

        when(fileReader.readFileTree("/path/dirA")).thenReturn(dirA);
        when(fileReader.readFileTree("/path/dirB")).thenReturn(dirB);

        var result = localFileService.getDiffHandler("user1", "/path/dirA", "/path/dirB");

        assertEquals(1, result.shared().size(), "Should have 1 shared file");
        assertEquals(1, result.onlyPathA().size(), "Should have 1 file only in A");
        assertEquals(1, result.onlyPathB().size(), "Should have 1 file only in B");

        assertTrue(result.shared().stream().anyMatch(e -> e.name().equals("file1.txt")));
        assertTrue(result.onlyPathA().stream().anyMatch(e -> e.name().equals("file2.txt")));
        assertTrue(result.onlyPathB().stream().anyMatch(e -> e.name().equals("file3.txt")));
    }
}
