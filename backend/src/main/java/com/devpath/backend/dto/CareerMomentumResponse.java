package com.devpath.backend.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CareerMomentumResponse {

    private String strongestRoadmap;
    private int strongestRoadmapProgress;

    private String weakestRoadmap;
    private int weakestRoadmapProgress;

    private int totalOverdueTasks;

    private String nearestCompletionRoadmap;
    private int nearestCompletionPercentage;

    private int momentumScore;
}