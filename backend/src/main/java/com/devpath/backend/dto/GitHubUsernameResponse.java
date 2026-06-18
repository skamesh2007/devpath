package com.devpath.backend.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@AllArgsConstructor
public class GitHubUsernameResponse {

    private String githubUsername;
}