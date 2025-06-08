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

import java.util.ArrayList;
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

    public DiffResponseDTO getDiffHandler(String username, String pathA, String pathB) {
        LocalFile localFileA = fileReader.readFileTree(pathA);
        LocalFile localFileB = fileReader.readFileTree(pathB);
        DiffRequest request = new DiffRequest(username, localFileA, localFileB);
        diffRequestRepository.save(request);
        return compareFiles(localFileA, localFileB);
    }

    public DiffResponseDTO compareFiles(LocalFile localFileA, LocalFile localFileB) {
        String pathA = localFileA.getPath();
        String pathB = localFileB.getPath();

        Set<EntryDTO> sharedFiles = new HashSet<>();
        Set<EntryDTO> onlyInA = new HashSet<>();
        Set<EntryDTO> onlyInB = new HashSet<>();

        if (localFileA instanceof Directory dirA && localFileB instanceof Directory dirB) {
            Map<String, LocalFile> filesA = dirA.getAllNestedFilesWithRelativePaths("");
            Map<String, LocalFile> filesB = dirB.getAllNestedFilesWithRelativePaths("");

            if (filesA.size() <= filesB.size()) {
                classifyDifferences(filesA, filesB, sharedFiles, onlyInA, onlyInB);
            } else {
                classifyDifferences(filesB, filesA, sharedFiles, onlyInB, onlyInA);
            }
            return new DiffResponseDTO(pathA, pathB, onlyInA, onlyInB, sharedFiles);
        }

        Set<EntryDTO> fallbackShared = handleFallback(localFileA, localFileB, onlyInA, onlyInB);

        return new DiffResponseDTO(pathA, pathB, onlyInA, onlyInB, fallbackShared);
    }

    private void classifyDifferences(
            Map<String, LocalFile> filesA,
            Map<String, LocalFile> filesB,
            Set<EntryDTO> shared,
            Set<EntryDTO> onlyInA,
            Set<EntryDTO> onlyInB) {

        for (Map.Entry<String, LocalFile> entry : filesA.entrySet()) {
            String path = entry.getKey();
            LocalFile fileA = entry.getValue();
            LocalFile fileB = filesB.get(path);

            if (fileB != null) {
                if (fileA.getSize() == fileB.getSize()) {
                    shared.add(new EntryDTO(path, fileA.getSize()));
                } else {
                    onlyInA.add(new EntryDTO(path, fileA.getSize()));
                    onlyInB.add(new EntryDTO(path, fileB.getSize()));
                }
            } else {
                onlyInA.add(new EntryDTO(path, fileA.getSize()));
            }
        }

        for (Map.Entry<String, LocalFile> entry : filesB.entrySet()) {
            String path = entry.getKey();
            if (!filesA.containsKey(path)) {
                onlyInB.add(new EntryDTO(path, entry.getValue().getSize()));
            }
        }
    }

    private Set<EntryDTO> handleFallback(
            LocalFile localFileA,
            LocalFile localFileB,
            Set<EntryDTO> onlyInA,
            Set<EntryDTO> onlyInB) {
        Set<EntryDTO> fallbackShared = new HashSet<>();
        if (localFileA.getName().equals(localFileB.getName())
                && localFileA.getSize() == localFileB.getSize()) {
            fallbackShared.add(new EntryDTO(localFileA.getName(), localFileA.getSize()));
        } else {
            onlyInA.add(new EntryDTO(localFileA.getName(), localFileA.getSize()));
            onlyInB.add(new EntryDTO(localFileB.getName(), localFileB.getSize()));
        }
        return fallbackShared;
    }
}
