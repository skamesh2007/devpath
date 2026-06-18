package com.devpath.backend.controller;

import com.devpath.backend.dto.*;
import com.devpath.backend.service.GitHubService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/github")
@RequiredArgsConstructor
public class GitHubController {

    private final GitHubService gitHubService;

    @GetMapping("/username")
    public ResponseEntity<GitHubUsernameResponse> getUsername() {
        return ResponseEntity.ok(
                gitHubService.getUsername()
        );
    }

    @PostMapping("/username")
    public ResponseEntity<GitHubUsernameResponse> saveUsername(
            @RequestBody GitHubUsernameRequest request
    ) {
        return ResponseEntity.ok(
                gitHubService.saveUsername(request)
        );
    }

    @GetMapping("/stats")
    public ResponseEntity<GitHubStatsResponse> getStats() {
        return ResponseEntity.ok(
                gitHubService.getStats()
        );
    }

    @GetMapping("/repositories")
    public ResponseEntity<GitHubRepositoriesResponse> getRepositories() {
        return ResponseEntity.ok(
                gitHubService.getRepositories()
        );
    }

    @GetMapping("/languages")
    public ResponseEntity<GitHubLanguagesResponse> getLanguages() {

        return ResponseEntity.ok(
                gitHubService.getLanguages()
        );
    }

    @GetMapping("/activity")
    public ResponseEntity<GitHubActivityResponse> getActivity() {

        return ResponseEntity.ok(
                gitHubService.getActivity()
        );
    }
}