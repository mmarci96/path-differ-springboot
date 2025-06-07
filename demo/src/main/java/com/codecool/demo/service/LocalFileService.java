package com.codecool.demo.service;

import com.codecool.demo.repository.DirectoryRepository;
import com.codecool.demo.repository.LocalFileRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class LocalFileService {
    private final DirectoryRepository directoryRepository;
    private final LocalFileRepository localFileRepository;

    @Autowired
    public LocalFileService(
            DirectoryRepository directoryRepository, LocalFileRepository localFileRepository) {
        this.localFileRepository = localFileRepository;
        this.directoryRepository = directoryRepository;
    }

    public void printFiles(String path) {
        System.out.println(path);
    }
}
