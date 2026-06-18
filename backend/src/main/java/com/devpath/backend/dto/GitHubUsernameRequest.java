package com.devpath.backend.dto;

import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class GitHubUsernameRequest {

    @Size(max = 100)
    private String githubUsername;
}