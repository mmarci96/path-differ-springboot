package com.codecool.demo.controller;

import com.codecool.demo.service.LocalFileService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.view.RedirectView;

/**
 * REST controller for handling operations related to local file structure comparison. Provides
 * endpoints to compare directories, view comparison history, and perform test requests.
 */
@RestController
public class LocalFileController {

    private final LocalFileService localFileService;

    /**
     * Constructs the controller with the provided {@link LocalFileService}.
     *
     * @param localFileService the service handling file comparison logic
     */
    @Autowired
    public LocalFileController(LocalFileService localFileService) {
        this.localFileService = localFileService;
    }

    /**
     * Compares two file system paths for a specific user and returns the differences.
     *
     * @param username the user requesting the comparison
     * @param pathA the path to the first directory
     * @param pathB the path to the second directory
     * @return a {@link ResponseEntity} containing the diff result
     */
    @GetMapping("/get-diff/{username}")
    public ResponseEntity<?> getDiff(
            @PathVariable String username, @RequestParam String pathA, @RequestParam String pathB) {
        var diffResult = localFileService.getDiffHandler(username, pathA, pathB);
        return ResponseEntity.ok(diffResult);
    }

    /**
     * Retrieves the history of previous directory comparisons.
     *
     * @return a {@link ResponseEntity} containing the comparison history
     */
    @GetMapping("/history")
    public ResponseEntity<?> getHistory() {
        return ResponseEntity.ok(localFileService.getHistory());
    }

    /**
     * Test endpoint for connectivity checks.
     *
     * @return a {@link ResponseEntity} with a simple OK message
     */
    @GetMapping("/test")
    public ResponseEntity<?> getTest() {
        return ResponseEntity.ok("OK");
    }

    /**
     * Endpoint to fetch the java docs of the app.
     *
     * @return a {@link RedirectView} with index.html file.
     */
    @GetMapping("/doc")
    public RedirectView redirectToJavadoc() {
        return new RedirectView("/doc/javadoc/index.html");
    }
}
