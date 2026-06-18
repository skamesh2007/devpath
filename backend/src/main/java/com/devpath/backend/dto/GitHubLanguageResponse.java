package com.devpath.backend.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class GitHubLanguageResponse {

    private String language;

    private int repositoryCount;

    private double percentage;
}