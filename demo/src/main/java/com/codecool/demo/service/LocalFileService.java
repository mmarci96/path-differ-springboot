package com.codecool.demo.service;

import com.codecool.demo.dto.DiffResponseDTO;
import com.codecool.demo.dto.HistoryEntryDTO;
import com.codecool.demo.exception.LocalFileNotFoundException;
import com.codecool.demo.model.DiffEntry;
import com.codecool.demo.model.DiffRequest;
import com.codecool.demo.model.Directory;
import com.codecool.demo.model.LocalFile;
import com.codecool.demo.repository.DiffRequestRepository;
import com.codecool.demo.repository.DirectoryRepository;
import com.codecool.demo.repository.LocalFileRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.File;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class LocalFileService {
    private final DirectoryRepository directoryRepository;
    private final LocalFileRepository localFileRepository;
    private final DiffRequestRepository diffRequestRepository;

    @Autowired
    public LocalFileService(
            DirectoryRepository directoryRepository,
            LocalFileRepository localFileRepository,
            DiffRequestRepository diffRequestRepository) {
        this.localFileRepository = localFileRepository;
        this.directoryRepository = directoryRepository;
        this.diffRequestRepository = diffRequestRepository;
    }

    public  List<HistoryEntryDTO> getHistory() {
        return diffRequestRepository.findAll().stream().map(DiffRequest::toHistoryDTO).toList();
    }

    public DiffResponseDTO compareFiles(String username, String pathA, String pathB) {
        LocalFile localFileA = processFile(pathA);
        LocalFile localFileB = processFile(pathB);
        localFileRepository.save(localFileA);
        localFileRepository.save(localFileB);

        DiffRequest diffRequest = new DiffRequest(username, localFileA, localFileB);
        collectDifferences(localFileA, localFileB, diffRequest, "");
        diffRequestRepository.save(diffRequest);

        var differencies =
                diffRequest.getDifferences().stream().map(DiffEntry::toDiffEntryDTO).toList();
        return new DiffResponseDTO(pathA, pathB, differencies);
    }

    private LocalFile processFile(String path) {
        var file = new File(path);
        if (!file.isFile() && !file.isDirectory()) {
            throw new LocalFileNotFoundException("No file at: " + path);
        }

        if (file.isDirectory()) {
            Directory localDir = new Directory();
            localDir.setName(file.getName());
            localDir.setPath(file.getPath());
            directoryRepository.save(localDir);

            File[] files = file.listFiles();
            long dirSize = 0;
            for (File currFile : files) {
                LocalFile currLocalFile = processFile(currFile.getPath());
                currLocalFile.setDirectory(localDir);
                localFileRepository.save(currLocalFile);
                dirSize = dirSize + currLocalFile.getSize();
                localDir.addLocalFile(currLocalFile);
            }

            localDir.setSize(dirSize);
            directoryRepository.save(localDir);
            return localDir;
        }

        LocalFile localFile = new LocalFile();
        localFile.setName(file.getName());
        localFile.setPath(file.getPath());
        localFile.setSize(file.length());
        localFileRepository.save(localFile);
        return localFile;
    }

    public void collectDifferences(
            LocalFile fileA, LocalFile fileB, DiffRequest request, String relativePath) {
        if (handleMissingFile(fileA, fileB, request, relativePath)) return;
        if (handleFileMismatch(fileA, fileB, request, relativePath)) return;

        if (fileA instanceof Directory dirA && fileB instanceof Directory dirB) {
            compareDirectories(dirA, dirB, request, relativePath);
        } else {
            compareFiles(fileA, fileB, request, relativePath);
        }
    }

    private boolean handleMissingFile(
            LocalFile fileA, LocalFile fileB, DiffRequest request, String path) {
        if (fileA == null && fileB == null) return true;

        if (fileA == null || fileB == null) {
            DiffEntry diff = new DiffEntry();
            diff.setPath(path);
            diff.setType("Missing");
            diff.setMessage(fileA == null ? "Missing in A" : "Missing in B");
            request.addDifference(diff);
            return true;
        }
        return false;
    }

    private boolean handleFileMismatch(
            LocalFile fileA, LocalFile fileB, DiffRequest request, String path) {
        if (fileA.getClass() != fileB.getClass()) {
            DiffEntry diff = new DiffEntry();
            diff.setPath(path);
            diff.setType("TypeMismatch");
            diff.setMessage("Type mismatch: " + fileA.getName() + " vs " + fileB.getName());
            request.addDifference(diff);
            return true;
        }
        if (!fileA.getName().equals(fileB.getName())) {
            DiffEntry diff = new DiffEntry();
            diff.setPath(path);
            diff.setType("NameMismatch");
            diff.setMessage("Name mismatch: " + fileA.getName() + " vs " + fileB.getName());
            request.addDifference(diff);
            return true;
        }

        return false;
    }

    private void compareFiles(LocalFile fileA, LocalFile fileB, DiffRequest request, String path) {
        if (fileA.getSize() != fileB.getSize()) {
            DiffEntry diff = new DiffEntry();
            diff.setPath(path);
            diff.setType("SizeMismatch");
            diff.setMessage("Size differs: " + fileA.getSize() + " vs " + fileB.getSize());
            request.addDifference(diff);
        }
    }

    private void compareDirectories(
            Directory dirA, Directory dirB, DiffRequest request, String basePath) {
        Map<String, LocalFile> mapB =
                dirB.getLocalFiles().stream().collect(Collectors.toMap(LocalFile::getName, f -> f));

        for (LocalFile childA : dirA.getLocalFiles()) {
            String childPath = basePath + "/" + childA.getName();
            LocalFile childB = mapB.get(childA.getName());
            collectDifferences(childA, childB, request, childPath);
        }

        Set<String> namesInA =
                dirA.getLocalFiles().stream().map(LocalFile::getName).collect(Collectors.toSet());

        for (LocalFile childB : dirB.getLocalFiles()) {
            if (!namesInA.contains(childB.getName())) {
                DiffEntry diff = new DiffEntry();
                diff.setPath(basePath + "/" + childB.getName());
                diff.setType("ExtraInB");
                diff.setMessage("File exists in B but not in A");
                request.addDifference(diff);
            }
        }
    }
}
