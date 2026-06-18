package com.devpath.backend.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class GitHubStatsResponse {

    private String username;

    private String profileUrl;

    private String avatarUrl;

    private int publicRepos;

    private int followers;

    private int following;

    private int totalStars;
}