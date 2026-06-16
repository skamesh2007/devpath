package com.devpath.backend.dto;

import lombok.Data;
import java.util.List;

@Data
public class LeetCodeStatsResponse {
    private String username;
    private int ranking;
    private int totalSolved;
    private int easySolved;
    private int mediumSolved;
    private int hardSolved;
    private List<RecentSubmission> recentSubmissions;

    @Data
    public static class RecentSubmission {
        private String title;
        private String titleSlug;
        private String statusDisplay;
        private String lang;
        private String timestamp;
    }
}