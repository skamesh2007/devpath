package com.devpath.backend.dto.github;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class GitHubRepositoriesResponse {

    private int totalRepositories;

    private int totalStars;

    private List<GitHubRepositoryResponse> repositories;
}