package com.devpath.backend.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RoadmapAnalyticsResponse {

    private Long roadmapId;
    private String roadmapTitle;

    private int completionPercentage;
    private int completedTasks;
    private int remainingTasks;

    private int overdueTasks;
    private int highPriorityRemaining;

    private int estimatedHoursRemaining;
    private int projectedCompletionDays;

    private List<RecommendedTaskResponse> nextRecommendedTasks;
}