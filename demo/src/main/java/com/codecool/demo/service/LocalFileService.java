package com.codecool.demo.service;

import com.codecool.demo.model.DiffRequest;
import com.codecool.demo.model.Directory;
import com.codecool.demo.model.LocalFile;
import com.codecool.demo.repository.DiffRequestRepository;
import com.codecool.demo.repository.DirectoryRepository;
import com.codecool.demo.repository.LocalFileRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.File;
import java.time.LocalDateTime;

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
        File fileA = new File(pathA);
        LocalFile localFileA = processFile(fileA);
        File fileB = new File(pathB);
        LocalFile localFileB = processFile(fileB);
        localFileRepository.save(localFileA);
        localFileRepository.save(localFileB);

        DiffRequest request = new DiffRequest();
        request.setUsername(username);
        request.setLocalFileA(localFileA);
        request.setLocalFileB(localFileB);
        request.setCreatedAt(LocalDateTime.now());
        diffRequestRepository.save(request);

        return true;
    }

    private LocalFile processFile(File file) {
        printFileDetails(file);
        if (file.isDirectory()) {
            Directory localDir = new Directory();
            localDir.setName(file.getName());
            localDir.setPath(file.getPath());

            directoryRepository.save(localDir);

            long dirSize = 0;
            File[] files = file.listFiles();
            for (File currFile : files) {
                LocalFile currLocalFile = processFile(currFile);
                currLocalFile.setDirectory(localDir);
                localFileRepository.save(currLocalFile);
                dirSize = dirSize + currLocalFile.getSize();
                localDir.addLocalFile(currLocalFile);
            }

            localDir.setSize(dirSize);
            directoryRepository.save(localDir);
            return localDir;
        } else if (file.isFile()) {
            LocalFile localFile = new LocalFile();
            localFile.setName(file.getName());
            localFile.setPath(file.getPath());
            localFile.setSize(file.length());
            localFileRepository.save(localFile);
            return localFile;
        }
        return null;
    }

    private void printFileDetails(File file) {
        System.out.println("+----------------+--------------------------------------------------+");
        System.out.printf("| %-14s | %-48s |\n", "Property", "Value");
        System.out.println("+----------------+--------------------------------------------------+");
        System.out.printf("| %-14s | %-48s |\n", "Name", file.getName());
        System.out.printf("| %-14s | %-48s |\n", "Path", file.getPath());
        System.out.printf("| %-14s | %-48d |\n", "Size (bytes)", file.length());
        System.out.printf("| %-14s | %-48s |\n", "Is Directory", file.isDirectory());
        System.out.printf("| %-14s | %-48s |\n", "Is File", file.isFile());
        System.out.println("+----------------+--------------------------------------------------+");
    }
}
