package com.codecool.demo.service;

import com.codecool.demo.exception.LocalFileNotFoundException;
import com.codecool.demo.model.DiffRequest;
import com.codecool.demo.model.Directory;
import com.codecool.demo.model.LocalFile;
import com.codecool.demo.repository.DiffRequestRepository;
import com.codecool.demo.repository.DirectoryRepository;
import com.codecool.demo.repository.LocalFileRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.File;

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

    public boolean compareFiles(String username, String pathA, String pathB) {
        LocalFile localFileA = processFile(pathA);
        LocalFile localFileB = processFile(pathB);
        localFileRepository.save(localFileA);
        localFileRepository.save(localFileB);

        DiffRequest diffRequest = new DiffRequest(username, localFileA, localFileB);
        diffRequestRepository.save(diffRequest);
        return false;
        // return areFilesStructurallyEqual(localFileA, localFileB);
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
    //
    // public boolean areFilesStructurallyEqual(LocalFile fileA, LocalFile fileB) {
    //     if (fileA == null || fileB == null) return false;
    //
    //     if (!fileA.getName().equals(fileB.getName())) return false;
    //     if (fileA.getClass() != fileB.getClass()) return false;
    //
    //     if (!(fileA instanceof Directory)) return true;
    //
    //     Directory dirA = (Directory) fileA;
    //     Directory dirB = (Directory) fileB;
    //
    //     Set<LocalFile> contentsA = dirA.getLocalFiles();
    //     Set<LocalFile> contentsB = dirB.getLocalFiles();
    //
    //     if (contentsA.size() != contentsB.size()) return false;
    //
    //     for (LocalFile childA : contentsA) {
    //         boolean matchFound =
    //                 contentsB.stream()
    //                         .anyMatch(childB -> areFilesStructurallyEqual(childA, childB));
    //         if (!matchFound) return false;
    //     }
    //
    //     return true;
    // }
}
