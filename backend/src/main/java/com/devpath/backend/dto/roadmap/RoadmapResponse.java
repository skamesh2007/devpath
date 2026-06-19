package com.devpath.backend.dto.roadmap;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class RoadmapResponse {

    private Long id;

    private String title;

    private String description;

    private int totalTasks;

    private int completedTasks;

    private int progress;
}