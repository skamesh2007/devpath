package com.devpath.backend.controller;

import com.devpath.backend.dto.LeetCodeStatsResponse;
import com.devpath.backend.dto.LeetCodeUsernameRequest;
import com.devpath.backend.service.LeetCodeService;
import com.fasterxml.jackson.core.JsonProcessingException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/leetcode")
public class LeetCodeController {

    private final LeetCodeService leetCodeService;

    @PostMapping("/username")
    public ResponseEntity<String> saveUsername(
            @RequestBody LeetCodeUsernameRequest request,
            Authentication authentication
    ) {

        String username = authentication.getName();

        leetCodeService.saveLeetcodeUsername(
                username,
                request.getLeetcodeUsername()
        );

        if (request.getLeetcodeUsername() == null ||
                request.getLeetcodeUsername().trim().isEmpty()) {
            return ResponseEntity.ok("LeetCode username removed");
        }

        return ResponseEntity.ok("LeetCode username saved");
    }

    @GetMapping("/username")
    public ResponseEntity<Map<String, String>> getUsername(
            Authentication authentication
    ) {

        String username = authentication.getName();

        Map<String, String> response = new HashMap<>();
        response.put(
                "username",
                leetCodeService.getLeetCodeUsername(username)
        );

        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/username")
    public ResponseEntity<Void> removeUsername(
            Authentication authentication
    ) {

        String username = authentication.getName();

        leetCodeService.removeLeetCodeUsername(username);

        return ResponseEntity.noContent().build();
    }

    @GetMapping("/me")
    public ResponseEntity<LeetCodeStatsResponse> getMyStats(
            Authentication authentication
    ) throws JsonProcessingException {

        String username = authentication.getName();

        return ResponseEntity.ok(
                leetCodeService.getStatsForCurrentUser(username)
        );
    }

    @GetMapping("/{username}")
    public ResponseEntity<LeetCodeStatsResponse> getLeetCodeProfile(
            @PathVariable String username
    ) throws JsonProcessingException {

        return ResponseEntity.ok(
                leetCodeService.fetchStats(username)
        );
    }
}