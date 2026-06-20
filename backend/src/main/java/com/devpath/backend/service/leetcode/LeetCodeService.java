package com.devpath.backend.service.leetcode;

import com.devpath.backend.dto.leetcode.LeetCodeStatsResponse;
import com.devpath.backend.entity.LeetCodeStats;
import com.devpath.backend.entity.RecentSubmission;
import com.devpath.backend.entity.User;
import com.devpath.backend.repository.LeetCodeStatsRepository;
import com.devpath.backend.repository.UserRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import lombok.RequiredArgsConstructor;
import org.springframework.http.*;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;
import com.fasterxml.jackson.databind.ObjectMapper;


import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class LeetCodeService {

    private final UserRepository userRepository;
    private final LeetCodeStatsRepository leetCodeStatsRepository;
    private final ObjectMapper objectMapper;

    private static final String LEETCODE_GRAPHQL = "https://leetcode.com/graphql";

    // ── Save username ─────────────────────────────────────────────────────────
    // 'username' here is the Spring Security principal name (= JWT subject = User.username)
    public void saveLeetcodeUsername(String username, String leetcodeUsername) throws JsonProcessingException {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() ->
                        new UsernameNotFoundException("User not found: " + username));

        if (leetcodeUsername == null || leetcodeUsername.trim().isEmpty()) {
            user.setLeetCodeUsername(null);
            userRepository.save(user);
            leetCodeStatsRepository.findByUser(user)
                    .ifPresent(leetCodeStatsRepository::delete);
            return;
        }

        user.setLeetCodeUsername(leetcodeUsername.trim());
        userRepository.save(user);
        refreshLeetCodeStats(user);
    }

    // ── Fetch the stored LeetCode username for a given app user ───────────────
    public String getLeetCodeUsername(String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() ->
                        new UsernameNotFoundException("User not found: " + username));

        return user.getLeetCodeUsername();
    }

    // ── Fetch stats for the logged-in user's stored LeetCode username ─────────
    public LeetCodeStats getStatsForCurrentUser(User user) {

        LeetCodeStats stats = leetCodeStatsRepository
                .findByUser(user)
                .orElse(null);

        
        return stats;
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



        JsonNode root = objectMapper.readTree(response.getBody());

        System.out.println("Root" + root);

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
        System.out.println("DTO" + dto);
        return dto;
    }



    public void removeLeetCodeUsername(String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() ->
                        new UsernameNotFoundException("User not found: " + username));

        user.setLeetCodeUsername(null);
        userRepository.save(user);

        leetCodeStatsRepository.findByUser(user)
                .ifPresent(leetCodeStatsRepository::delete);
    }

    @Transactional
    public void refreshLeetCodeStats(User user)
            throws JsonProcessingException {

        if (user.getLeetCodeUsername() == null ||
                user.getLeetCodeUsername().isBlank()) {
            return;
        }

        LeetCodeStatsResponse response =
                fetchStats(user.getLeetCodeUsername());

        System.out.println("Response" + response);

        LeetCodeStats stats = leetCodeStatsRepository
                .findByUser(user)
                .orElseGet(() -> LeetCodeStats.builder()
                        .user(user)
                        .build());

        stats.setRanking(response.getRanking());
        stats.setTotalSolved(response.getTotalSolved());
        stats.setEasySolved(response.getEasySolved());
        stats.setMediumSolved(response.getMediumSolved());
        stats.setHardSolved(response.getHardSolved());
        stats.setLastUpdated(LocalDateTime.now());
        stats.setUsername(response.getUsername());
        stats.getRecentSubmissions().clear();

        for (LeetCodeStatsResponse.RecentSubmission dto :
                response.getRecentSubmissions()) {

            RecentSubmission submission = RecentSubmission.builder()
                    .title(dto.getTitle())
                    .titleSlug(dto.getTitleSlug())
                    .statusDisplay(dto.getStatusDisplay())
                    .lang(dto.getLang())
                    .timestamp(dto.getTimestamp())
                    .leetCodeStats(stats)
                    .build();

            stats.getRecentSubmissions().add(submission);
        }

        System.out.println("Stats" + stats);

        leetCodeStatsRepository.save(stats);
    }



}