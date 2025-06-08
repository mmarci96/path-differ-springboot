package com.codecool.demo.service;

import com.codecool.demo.dto.DiffResponseDTO;
import com.codecool.demo.dto.EntryDTO;
import com.codecool.demo.dto.HistoryEntryDTO;
import com.codecool.demo.model.DiffRequest;
import com.codecool.demo.model.Directory;
import com.codecool.demo.model.LocalFile;
import com.codecool.demo.repository.DiffRequestRepository;
import com.codecool.demo.util.LocalFileReader;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Service
public class LocalFileService {
    private final LocalFileReader fileReader;
    private final DiffRequestRepository diffRequestRepository;

    @Autowired
    public LocalFileService(
            LocalFileReader fileReader, DiffRequestRepository diffRequestRepository) {
        this.diffRequestRepository = diffRequestRepository;
        this.fileReader = fileReader;
    }

    public List<HistoryEntryDTO> getHistory() {
        return diffRequestRepository.findAll().stream().map(DiffRequest::toHistoryDTO).toList();
    }

    public DiffResponseDTO compareFiles(String username, String pathA, String pathB) {
        LocalFile localFileA = fileReader.readFileTree(pathA);
        LocalFile localFileB = fileReader.readFileTree(pathB);

        Set<EntryDTO> sharedFiles = new HashSet<>();
        Set<EntryDTO> onlyInA = new HashSet<>();
        Set<EntryDTO> onlyInB = new HashSet<>();

        if (localFileA instanceof Directory dirA && localFileB instanceof Directory dirB) {

            Map<String, LocalFile> filesA = dirA.getAllNestedFilesWithRelativePaths("");
            Map<String, LocalFile> filesB = dirB.getAllNestedFilesWithRelativePaths("");

            Set<String> allPaths = new HashSet<>(filesA.keySet());
            allPaths.addAll(filesB.keySet());

            for (String path : allPaths) {
                LocalFile fileA = filesA.get(path);
                LocalFile fileB = filesB.get(path);

                if (fileA != null && fileB != null) {
                    if (fileA.getSize() == fileB.getSize()) {
                        sharedFiles.add(new EntryDTO(path, fileA.getSize()));
                    } else {
                        onlyInA.add(new EntryDTO(path, fileA.getSize()));
                        onlyInB.add(new EntryDTO(path, fileB.getSize()));
                    }
                } else if (fileA != null) {
                    onlyInA.add(new EntryDTO(path, fileA.getSize()));
                } else {
                    onlyInB.add(new EntryDTO(path, fileB.getSize()));
                }
            }

            return new DiffResponseDTO(pathA, pathB, onlyInA, onlyInB, sharedFiles);
        }

        // fallback for non-directory files
        Set<EntryDTO> fallbackShared = new HashSet<>();
        if (localFileA.getName().equals(localFileB.getName())
                && localFileA.getSize() == localFileB.getSize()) {
            fallbackShared.add(new EntryDTO(localFileA.getName(), localFileA.getSize()));
        } else {
            onlyInA.add(new EntryDTO(localFileA.getName(), localFileA.getSize()));
            onlyInB.add(new EntryDTO(localFileB.getName(), localFileB.getSize()));
        }

        return new DiffResponseDTO(pathA, pathB, onlyInA, onlyInB, fallbackShared);
    }
    //
    // public void collectDifferences(
    //     LocalFile fileA, LocalFile fileB, DiffRequest request, String relativePath) {
    //   if (handleMissingFile(fileA, fileB, request, relativePath)) return;
    //   if (handleFileMismatch(fileA, fileB, request, relativePath)) return;
    //
    //   if (fileA instanceof Directory dirA && fileB instanceof Directory dirB) {
    //     compareDirectories(dirA, dirB, request, relativePath);
    //   } else {
    //     compareFiles(fileA, fileB, request, relativePath);
    //   }
    // }
    //
    // private boolean handleMissingFile(
    //     LocalFile fileA, LocalFile fileB, DiffRequest request, String path) {
    //   if (fileA == null && fileB == null) return true;
    //
    //   if (fileA == null || fileB == null) {
    //     DiffEntry diff = new DiffEntry();
    //     diff.setPath(path);
    //     diff.setType("Missing");
    //     diff.setMessage(fileA == null ? "Missing in A" : "Missing in B");
    //     request.addDifference(diff);
    //     return true;
    //   }
    //   return false;
    // }
    //
    // private boolean handleFileMismatch(
    //     LocalFile fileA, LocalFile fileB, DiffRequest request, String path) {
    //   if (fileA.getClass() != fileB.getClass()) {
    //     DiffEntry diff = new DiffEntry();
    //     diff.setPath(path);
    //     diff.setType("TypeMismatch");
    //     diff.setMessage("Type mismatch: " + fileA.getName() + " vs " + fileB.getName());
    //     request.addDifference(diff);
    //     return true;
    //   }
    //   if (!fileA.getName().equals(fileB.getName())) {
    //     DiffEntry diff = new DiffEntry();
    //     diff.setPath(path);
    //     diff.setType("NameMismatch");
    //     diff.setMessage("Name mismatch: " + fileA.getName() + " vs " + fileB.getName());
    //     request.addDifference(diff);
    //     return true;
    //   }
    //
    //   return false;
    // }
    //
    // private void compareFiles(LocalFile fileA, LocalFile fileB, DiffRequest request, String path)
    // {
    //   if (fileA.getSize() != fileB.getSize()) {
    //     DiffEntry diff = new DiffEntry();
    //     diff.setPath(path);
    //     diff.setType("SizeMismatch");
    //     diff.setMessage("Size differs: " + fileA.getSize() + " vs " + fileB.getSize());
    //     request.addDifference(diff);
    //   }
    // }
    //
    // private void compareDirectories(
    //     Directory dirA, Directory dirB, DiffRequest request, String basePath) {
    //   Map<String, LocalFile> mapB =
    //       dirB.getLocalFiles().stream().collect(Collectors.toMap(LocalFile::getName, f -> f));
    //
    //   for (LocalFile childA : dirA.getLocalFiles()) {
    //     String childPath = basePath + "/" + childA.getName();
    //     LocalFile childB = mapB.get(childA.getName());
    //     collectDifferences(childA, childB, request, childPath);
    //   }
    //
    //   Set<String> namesInA =
    //       dirA.getLocalFiles().stream().map(LocalFile::getName).collect(Collectors.toSet());
    //
    //   for (LocalFile childB : dirB.getLocalFiles()) {
    //     if (!namesInA.contains(childB.getName())) {
    //       DiffEntry diff = new DiffEntry();
    //       diff.setPath(basePath + "/" + childB.getName());
    //       diff.setType("ExtraInB");
    //       diff.setMessage("File exists in B but not in A");
    //       request.addDifference(diff);
    //     }
    //   }
    // }
}
