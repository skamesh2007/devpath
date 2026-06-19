package com.devpath.backend.dto.roadmap.airoadmap;

import com.devpath.backend.entity.TaskPriority;
import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AIRoadmapTask {

    private String title;
    private TaskPriority priority;
    private Integer estimatedHours;
    private Integer dueInDays;
}