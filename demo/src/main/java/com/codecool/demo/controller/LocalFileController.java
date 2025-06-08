package com.codecool.demo.controller;

import com.codecool.demo.service.LocalFileService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/files")
public class LocalFileController {
    private LocalFileService localFileService;

    @Autowired
    public LocalFileController(LocalFileService localFileService) {
        this.localFileService = localFileService;
    }

    @GetMapping("/get-diff/{username}")
    public ResponseEntity<?> getDiff(
            @PathVariable String username, @RequestParam String pathA, @RequestParam String pathB) {
        var isMatch = localFileService.getDiffHandler(username, pathA, pathB);

        return ResponseEntity.ok(isMatch);
    }

    @GetMapping("/history")
    public ResponseEntity<?> getHistory() {
        return ResponseEntity.ok(localFileService.getHistory());
    }

    @GetMapping("/test")
    public ResponseEntity<?> getTest() {
        return ResponseEntity.ok("OK");
    }
}
