package com.codecool.demo.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import com.codecool.demo.exception.LocalFileNotFoundException;
import com.codecool.demo.model.Directory;
import com.codecool.demo.model.LocalFile;
import com.codecool.demo.repository.DiffRequestRepository;
import com.codecool.demo.repository.DirectoryRepository;
import com.codecool.demo.repository.LocalFileRepository;
import com.codecool.demo.util.LocalFileReader;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Map;

@ExtendWith(MockitoExtension.class)
public class LocalFileServiceTest {

    @Mock private LocalFileReader fileReader;
    @Mock private LocalFileRepository localFileRepository;
    @Mock private DirectoryRepository directoryRepository;

    @Mock private DiffRequestRepository diffRequestRepository;

    @InjectMocks private LocalFileService localFileService;

    @Test
    void compareFiles_shouldClassifyDifferencesCorrectly() {
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

    @Test
    void getDiffHandler_shouldThrow_whenSourceFileDoesNotExist() {
        String invalidPathA = "/nonexistent/fileA";
        String validPathB = "/path/dirB";

        when(fileReader.readFileTree(invalidPathA))
                .thenThrow(new LocalFileNotFoundException("No file at: " + invalidPathA));

        var ex =
                assertThrows(
                        LocalFileNotFoundException.class,
                        () -> localFileService.getDiffHandler("user1", invalidPathA, validPathB),
                        "Expected exception for nonexistent fileA");

        assertTrue(ex.getMessage().contains(invalidPathA));
    }

    @Test
    void getDiffHandler_shouldThrow_whenTargetFolderDoesNotExist() {
        String validPathA = "/path/dirA";
        String invalidPathB = "/nonexistent/dirB";

        var dirA = mock(Directory.class);
        when(fileReader.readFileTree(validPathA)).thenReturn(dirA);

        when(fileReader.readFileTree(invalidPathB))
                .thenThrow(new LocalFileNotFoundException("No file at: " + invalidPathB));

        var ex =
                assertThrows(
                        LocalFileNotFoundException.class,
                        () -> localFileService.getDiffHandler("user1", validPathA, invalidPathB),
                        "Expected exception for nonexistent dirB");

        assertTrue(ex.getMessage().contains(invalidPathB));
    }
}
