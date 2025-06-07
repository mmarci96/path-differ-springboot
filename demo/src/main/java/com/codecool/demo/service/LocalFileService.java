package com.codecool.demo.service;

import com.codecool.demo.repository.DirectoryRepository;
import com.codecool.demo.repository.LocalFileRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.File;

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

    public boolean compareFiles(String pathA, String pathB) {
        System.out.println("PathA: " + pathA);
        System.out.println("PathB: " + pathB);
        File file1 = new File(pathA);
        processFile(file1);
        // File file2 = new File(pathB);
        // printFileDetails(file1);
        // printFileDetails(file2);

        return true;
    }

    private void processFile(File file){
        // File file = new File(path);
        if (file.isDirectory()){
            File[] files = file.listFiles();
            System.out.println("Directory found: "+file.getName());
            for (File currFile : files) {
               processFile(currFile); 
            }
        } else if (file.isFile()) {
            printFileDetails(file);
        }
    }

    private void printFileDetails(File file) {
        System.out.println("+----------------+--------------------------+");
        System.out.printf("| %-14s | %-24s |\n", "Property", "Value");
        System.out.println("+----------------+--------------------------+");
        System.out.printf("| %-14s | %-24s |\n", "Name", file.getName());
        System.out.printf("| %-14s | %-24d |\n", "Size (bytes)", file.length());
        System.out.printf("| %-14s | %-24s |\n", "Is Directory", file.isDirectory());
        System.out.printf("| %-14s | %-24s |\n", "Is File", file.isFile());
        System.out.println("+----------------+--------------------------+");
    }
}
