package com.devpath.backend.dto.github;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class GitHubActivityResponse {

    private int repositoriesUpdatedLast30Days;

    private int repositoriesCreatedLast30Days;

    private int activeRepositories;

    private String mostRecentlyUpdatedRepository;
}