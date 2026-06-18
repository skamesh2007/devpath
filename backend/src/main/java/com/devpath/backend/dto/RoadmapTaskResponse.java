package com.devpath.backend.dto;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;
import com.devpath.backend.entity.TaskPriority;

@Data
@Builder
public class RoadmapTaskResponse {

    private Long id;

    private String title;

    private String description;

    private Boolean completed;

    private LocalDate dueDate;
    private TaskPriority priority;
    private Integer estimatedHours;
}