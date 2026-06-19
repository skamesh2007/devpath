package com.devpath.backend.dto.roadmap.task;

import com.devpath.backend.entity.TaskPriority;
import lombok.Data;

import java.time.LocalDate;

@Data
public class UpdateTaskRequest {

    private String title;
    private String description;
    private Boolean completed;

    private LocalDate dueDate;
    private TaskPriority priority;
    private Integer estimatedHours;
}