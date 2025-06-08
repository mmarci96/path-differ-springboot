package com.codecool.demo.util;

import com.codecool.demo.exception.LocalFileNotFoundException;
import com.codecool.demo.model.Directory;
import com.codecool.demo.model.LocalFile;
import com.codecool.demo.repository.DirectoryRepository;
import com.codecool.demo.repository.LocalFileRepository;

import org.springframework.stereotype.Component;

import java.io.File;

@Component
public class LocalFileReaderImpl implements LocalFileReader {

    private final LocalFileRepository localFileRepository;
    private final DirectoryRepository directoryRepository;

    public LocalFileReaderImpl(
            LocalFileRepository localFileRepository, DirectoryRepository directoryRepository) {
        this.localFileRepository = localFileRepository;
        this.directoryRepository = directoryRepository;
    }

    @Override
    public LocalFile readFileTree(String path) {
        File file = new File(path);
        if (!file.exists()) {
            throw new LocalFileNotFoundException("No file at: " + path);
        }
        return process(file);
    }

    private LocalFile process(File file) {
        if (file.isDirectory()) {
            Directory directory = new Directory();
            directory.setName(file.getName());
            directory.setPath(file.getPath());
            directoryRepository.save(directory);

            long totalSize = 0;
            File[] children = file.listFiles();

            if (children != null) {
                for (File child : children) {
                    LocalFile localChild = process(child);
                    localChild.setDirectory(directory);
                    localFileRepository.save(localChild);
                    directory.addLocalFile(localChild);
                    totalSize += localChild.getSize();
                }
            }

            directory.setSize(totalSize);
            directoryRepository.save(directory);
            return directory;
        } else {
            LocalFile localFile = new LocalFile();
            localFile.setName(file.getName());
            localFile.setPath(file.getPath());
            localFile.setSize(file.length());
            localFileRepository.save(localFile);
            return localFile;
        }
    }
}
