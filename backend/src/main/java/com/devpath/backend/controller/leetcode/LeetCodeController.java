package com.devpath.backend.controller.leetcode;

import com.devpath.backend.dto.leetcode.LeetCodeUsernameRequest;
import com.devpath.backend.entity.LeetCodeStats;
import com.devpath.backend.entity.User;
import com.devpath.backend.repository.UserRepository;
import com.devpath.backend.service.leetcode.LeetCodeService;
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
    private final UserRepository userRepository;

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
    public ResponseEntity<LeetCodeStats> getMyStats(
            Authentication authentication
    ) throws JsonProcessingException {

        return ResponseEntity.ok(
                leetCodeService.getStatsForCurrentUser(
                        userRepository.findByUsername(authentication.getName())
                                .orElseThrow(() -> new IllegalArgumentException("User not found"))
                )
        );
    }

    @PostMapping("/refresh")
    public ResponseEntity<LeetCodeStats> refreshStats(
            Authentication authentication
    ) throws JsonProcessingException {

        String username = authentication.getName();

        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        leetCodeService.refreshLeetCodeStats(user);

        return ResponseEntity.ok(
                leetCodeService.getStatsForCurrentUser(user)
        );
    }

}