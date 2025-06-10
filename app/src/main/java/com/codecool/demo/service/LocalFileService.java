package com.codecool.demo.service;

import com.codecool.demo.dto.DiffResponseDTO;
import com.codecool.demo.dto.FileEntryDTO;
import com.codecool.demo.dto.HistoryEntryDTO;
import com.codecool.demo.model.DiffRequest;
import com.codecool.demo.model.Directory;
import com.codecool.demo.model.LocalFile;
import com.codecool.demo.repository.DiffRequestRepository;
import com.codecool.demo.repository.DirectoryRepository;
import com.codecool.demo.repository.LocalFileRepository;
import com.codecool.demo.util.LocalFileReader;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Service for comparing directory/file structures and managing comparison history. Handles reading
 * file trees, persisting comparison requests, and categorizing differences.
 *
 * <p>Retrieve full comparison history Process new comparison requests with automatic persistence
 * Compare two file structures (files/directories) and categorize differences
 */
@Service
public class LocalFileService {
    private final LocalFileReader fileReader;
    private final DiffRequestRepository diffRequestRepository;
    private final DirectoryRepository directoryRepository;
    private final LocalFileRepository localFileRepository;

    /**
     * Constructs a new {@code LocalFileService} instance with required dependencies.
     *
     * @param fileReader Utility for reading and parsing the file system structure into {@link
     *     LocalFile} objects
     * @param diffRequestRepository Repository for persisting and retrieving file comparison
     *     requests
     * @param directoryRepository Repository for persisting {@link Directory} structures
     * @param localFileRepository Repository for persisting {@link LocalFile} (non-directory)
     *     objects
     */
    @Autowired
    public LocalFileService(
            LocalFileReader fileReader,
            DiffRequestRepository diffRequestRepository,
            DirectoryRepository directoryRepository,
            LocalFileRepository localFileRepository) {
        this.diffRequestRepository = diffRequestRepository;
        this.fileReader = fileReader;
        this.directoryRepository = directoryRepository;
        this.localFileRepository = localFileRepository;
    }

    /**
     * Retrieves the complete history of file comparison requests. Each history entry contains: -
     * Username of the requester - Full comparison results ({@link DiffResponseDTO}) - Timestamp of
     * the request
     *
     * @return List of {@link HistoryEntryDTO} objects in chronological order (newest first based on
     *     repository ordering)
     */
    public List<HistoryEntryDTO> getHistory() {
        List<HistoryEntryDTO> entryList = new ArrayList<HistoryEntryDTO>();
        List<DiffRequest> diffRequests = diffRequestRepository.findAll().stream().toList();
        for (DiffRequest request : diffRequests) {
            LocalFile localFileA = request.getLocalFileA();
            LocalFile localFileB = request.getLocalFileB();
            DiffResponseDTO diff = compareFiles(localFileA, localFileB);

            var name = request.getUsername();
            var at = request.getCreatedAt();
            HistoryEntryDTO entryDTO = new HistoryEntryDTO(name, diff, at);
            entryList.add(entryDTO);
        }

        return entryList;
    }

    /**
     * Processes a new file/directory comparison request: Reads file structures from both paths
     * Persists the request with username and file metadata Compares the file structures
     *
     * @param username Identifier of the user initiating the request
     * @param pathA Absolute path to first directory/file
     * @param pathB Absolute path to second directory/file
     * @return {@link DiffResponseDTO} containing: - Base paths compared - Files unique to each
     *     location - Files common to both locations (with matching sizes)
     * @throws FileSystemAccessException If paths are invalid/unreadable (handled by fileReader)
     */
    @Transactional(rollbackFor = Exception.class)
    public DiffResponseDTO getDiffHandler(String username, String pathA, String pathB) {
        LocalFile localFileA = fileReader.readFileTree(pathA);
        LocalFile localFileB = fileReader.readFileTree(pathB);
        saveFiles(localFileA);
        saveFiles(localFileB);
        DiffRequest request = new DiffRequest(username, localFileA, localFileB);
        diffRequestRepository.save(request);
        return compareFiles(localFileA, localFileB);
    }

    /**
     * Compares two {@link LocalFile} instances (files or directories) and categorizes contents.
     *
     * <p>Directory vs Directory: Recursively compares all nested files by relative paths File vs
     * File: Treats as single-item comparison Mixed types: Automatically falls back to file
     * comparison logic Size matching: Files are considered "shared" only when sizes match exactly.
     *
     * @param localFileA First file/directory to compare
     * @param localFileB Second file/directory to compare
     * @return {@link DiffResponseDTO} with categorized entries. Returned sets:
     */
    private DiffResponseDTO compareFiles(LocalFile localFileA, LocalFile localFileB) {

        Set<FileEntryDTO> sharedFiles = new HashSet<>();
        Set<FileEntryDTO> onlyInA = new HashSet<>();
        Set<FileEntryDTO> onlyInB = new HashSet<>();

        if (localFileA instanceof Directory dirA && localFileB instanceof Directory dirB) {
            // Map<String, LocalFile> filesB = dirB.getAllNestedFilesWithRelativePaths("");
            long sizeA = dirA.getTotalChildCount();
            long sizeB = dirB.getTotalChildCount();

            if (sizeA <= sizeB) {
                Map<String, LocalFile> filesA = dirA.getMapOfNestedFilesWithRelativePaths("");
                Set<LocalFile> filesB = dirB.getSetOfNestedFilesWithRelativePaths("");
                classifyDifferences(filesA, filesB, sharedFiles, onlyInA, onlyInB);
            } else {
                Map<String, LocalFile> filesB = dirB.getMapOfNestedFilesWithRelativePaths("");
                Set<LocalFile> filesA = dirA.getSetOfNestedFilesWithRelativePaths("");
                classifyDifferences(filesB, filesA, sharedFiles, onlyInB, onlyInA);
            }
            return new DiffResponseDTO(
                    localFileA.getPath(), localFileB.getPath(), onlyInA, onlyInB, sharedFiles);
        }

        Set<FileEntryDTO> fallbackShared = handleFallback(localFileA, localFileB, onlyInA, onlyInB);

        return new DiffResponseDTO(
                localFileA.getPath(), localFileB.getPath(), onlyInA, onlyInB, fallbackShared);
    }

    /**
     * Internal helper to classify file differences between two directory maps. Optimized by
     * iterating over the smaller map first when comparing directories. Classification rules:
     *
     * <p>Present in both + same size → shared Present in both + different sizes → added to
     * <b>onlyInA</b> and <b>onlyInB</b> Present in only one map → added to corresponding unique set
     *
     * @param fileMapLonger Map of relative paths → files from first directory
     * @param fileSetShorter Map of relative paths → files from second directory
     * @param shared Output: Files with matching paths and sizes
     * @param onlyInLonger Output: Files only present in first directory
     * @param onlyInShorter Output: Files only present in second directory
     */
    private void classifyDifferences(
            Map<String, LocalFile> fileMapLonger,
            Set<LocalFile> fileSetShorter,
            Set<FileEntryDTO> shared,
            Set<FileEntryDTO> onlyInLonger,
            Set<FileEntryDTO> onlyInShorter) {

        Set<FileEntryDTO> unmatchedInShorter =
                fileSetShorter.stream()
                        .map(file -> new FileEntryDTO(file.getName(), file.getBytes()))
                        .collect(Collectors.toSet());

        for (LocalFile entry : fileSetShorter) {
            String path = entry.getPath();
            LocalFile fileA = entry;
            LocalFile fileB = fileMapLonger.get(path);

            if (fileB == null) {
                onlyInLonger.add(new FileEntryDTO(path, fileA.getBytes()));
                continue;
            }

            long sizeA = fileA.getBytes();
            long sizeB = fileB.getBytes();

            var entryA = new FileEntryDTO(path, sizeA);
            unmatchedInShorter.remove(entryA);

            if (sizeA == sizeB) {
                shared.add(entryA);
                continue;
            }

            onlyInLonger.add(entryA);
        }

        onlyInShorter.addAll(unmatchedInShorter);
    }

    /**
     * Handles non-directory comparisons (single files). Fallback logic:
     *
     * <p>Same filename + same size → considered "shared" Different filenames/sizes → added to both
     * unique sets
     *
     * @param localFileA First file to compare
     * @param localFileB Second file to compare
     * @param onlyInA Output: Will contain first file if not shared
     * @param onlyInB Output: Will contain second file if not shared
     * @return Set of {@link FileEntryDTO} shared files (single element if files match, empty
     *     otherwise)
     */
    private Set<FileEntryDTO> handleFallback(
            LocalFile localFileA,
            LocalFile localFileB,
            Set<FileEntryDTO> onlyInA,
            Set<FileEntryDTO> onlyInB) {
        Set<FileEntryDTO> fallbackShared = new HashSet<>();
        if (localFileA.getName().equals(localFileB.getName())
                && localFileA.getBytes() == localFileB.getBytes()) {
            fallbackShared.add(new FileEntryDTO(localFileA.getName(), localFileA.getBytes()));
        } else {
            onlyInA.add(new FileEntryDTO(localFileA.getName(), localFileA.getBytes()));
            onlyInB.add(new FileEntryDTO(localFileB.getName(), localFileB.getBytes()));
        }
        return fallbackShared;
    }

    /**
     * Persists a {@link LocalFile} or {@link Directory} instance into the appropriate repository.
     *
     * <p>If the file is a directory, it is saved using {@link DirectoryRepository}; otherwise, it
     * is saved using {@link LocalFileRepository}.
     *
     * @param file {@link LocalFile} or {@link Directory} to persist
     */
    private void saveFiles(LocalFile file) {
        if (file instanceof Directory directory) {
            directoryRepository.save(directory);
            return;
        }
        localFileRepository.save(file);
    }
}
