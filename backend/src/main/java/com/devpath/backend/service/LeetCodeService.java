package com.devpath.backend.service;

import com.devpath.backend.dto.LeetCodeStatsResponse;
import com.devpath.backend.entity.User;
import com.devpath.backend.repository.UserRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import lombok.RequiredArgsConstructor;
import org.springframework.http.*;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import com.fasterxml.jackson.databind.ObjectMapper;


import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class LeetCodeService {

    private final UserRepository userRepository;
    private static final String LEETCODE_GRAPHQL = "https://leetcode.com/graphql";

    // ── Save username ─────────────────────────────────────────────────────────
    // 'username' here is the Spring Security principal name (= JWT subject = User.username)
    public void saveLeetcodeUsername(String username, String leetcodeUsername) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() ->
                        new UsernameNotFoundException("User not found: " + username));

        if (leetcodeUsername == null || leetcodeUsername.trim().isEmpty()) {
            user.setLeetCodeUsername(null);
        } else {
            user.setLeetCodeUsername(leetcodeUsername.trim());
        }

        userRepository.save(user);
    }

    // ── Fetch stats for the logged-in user's stored LeetCode username ─────────
    public LeetCodeStatsResponse getStatsForCurrentUser(String username) throws JsonProcessingException {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found: " + username));

        if (user.getLeetCodeUsername() == null || user.getLeetCodeUsername().isBlank()) {
            throw new IllegalStateException("No LeetCode username linked to this account");
        }

        return fetchStats(user.getLeetCodeUsername());
    }

    // ── Fetch stats by any public LeetCode username ───────────────────────────
    public LeetCodeStatsResponse fetchStats(String username) throws JsonProcessingException {
        String query = """
            {
              "query": "{ matchedUser(username: \\"%s\\") { username profile { ranking } submitStats { acSubmissionNum { difficulty count } } } recentSubmissionList(username: \\"%s\\", limit: 10) { title titleSlug statusDisplay lang timestamp } }"
            }
        """.formatted(username, username);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("Referer", "https://leetcode.com");
        headers.set("User-Agent", "Mozilla/5.0");

        HttpEntity<String> request = new HttpEntity<>(query, headers);
        RestTemplate restTemplate = new RestTemplate();

        ResponseEntity<String> response = restTemplate.exchange(
                LEETCODE_GRAPHQL,
                HttpMethod.POST,
                request,
                String.class
        );

        ObjectMapper mapper = new ObjectMapper();

        JsonNode root = mapper.readTree(response.getBody());

        return mapToDto(root, username);
    }

    // ── Mapping ───────────────────────────────────────────────────────────────
    private LeetCodeStatsResponse mapToDto(JsonNode root, String username) {
        LeetCodeStatsResponse dto = new LeetCodeStatsResponse();
        dto.setUsername(username);

        JsonNode data = root.path("data");
        JsonNode user = data.path("matchedUser");

        dto.setRanking(user.path("profile").path("ranking").asInt());

        for (JsonNode stat : user.path("submitStats").path("acSubmissionNum")) {
            int count = stat.path("count").asInt();
            switch (stat.path("difficulty").asText()) {
                case "All"    -> dto.setTotalSolved(count);
                case "Easy"   -> dto.setEasySolved(count);
                case "Medium" -> dto.setMediumSolved(count);
                case "Hard"   -> dto.setHardSolved(count);
            }
        }

        List<LeetCodeStatsResponse.RecentSubmission> submissions = new ArrayList<>();
        for (JsonNode node : data.path("recentSubmissionList")) {
            LeetCodeStatsResponse.RecentSubmission sub = new LeetCodeStatsResponse.RecentSubmission();
            sub.setTitle(node.path("title").asText());
            sub.setTitleSlug(node.path("titleSlug").asText());
            sub.setStatusDisplay(node.path("statusDisplay").asText());
            sub.setLang(node.path("lang").asText());
            sub.setTimestamp(node.path("timestamp").asText());
            submissions.add(sub);
        }
        dto.setRecentSubmissions(submissions);

        return dto;
    }

    public String getLeetCodeUsername(String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() ->
                        new UsernameNotFoundException("User not found: " + username));

        return user.getLeetCodeUsername();
    }

    public void removeLeetCodeUsername(String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() ->
                        new UsernameNotFoundException("User not found: " + username));

        user.setLeetCodeUsername(null);
        userRepository.save(user);
    }
}