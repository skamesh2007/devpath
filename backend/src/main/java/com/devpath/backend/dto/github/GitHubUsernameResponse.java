package com.devpath.backend.dto.github;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@AllArgsConstructor
public class GitHubUsernameResponse {

    private String githubUsername;
}