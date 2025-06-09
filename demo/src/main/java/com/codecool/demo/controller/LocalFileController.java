package com.codecool.demo.controller;

import com.codecool.demo.dto.*;
import com.codecool.demo.service.LocalFileService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.view.RedirectView;

import java.util.List;

/**
 * REST controller for handling operations related to local file structure comparison. Provides
 * endpoints to compare directories, view comparison history, and perform test requests. Serves
 * static files of javadocs of this project.
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
     * Compares two file system paths for a specific user then returns the shared files and unique
     * ones for each path.
     *
     * @param username the user requesting the comparison
     * @param pathA the path to the first directory
     * @param pathB the path to the second directory
     * @return a {@link ResponseEntity} containing the diff result
     */
    @GetMapping("/get-diff/{username}")
    public ResponseEntity<DiffResponseDTO> getDiff(
            @PathVariable String username, @RequestParam String pathA, @RequestParam String pathB) {
        var diffResult = localFileService.getDiffHandler(username, pathA, pathB);
        return ResponseEntity.ok(diffResult);
    }

    /**
     * Retrieves the history of previous path comparisons, including the username that requested
     * them and the time of each request.
     *
     * @return a {@link ResponseEntity} containing a list of {@link HistoryEntryDTO} objects
     *     representing past comparisons.
     */
    @GetMapping("/history")
    public ResponseEntity<List<HistoryEntryDTO>> getHistory() {
        var history = localFileService.getHistory();
        return ResponseEntity.ok(history);
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

    /**
     * Test endpoint for connectivity checks.
     *
     * @return a {@link ResponseEntity} with a simple OK message
     */
    @GetMapping("/health")
    public ResponseEntity<String> getHealth() {
        return ResponseEntity.ok("OK");
    }
}
