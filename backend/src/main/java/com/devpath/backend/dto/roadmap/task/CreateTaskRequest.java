package com.devpath.backend.dto.roadmap.task;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import java.time.LocalDate;
import com.devpath.backend.entity.TaskPriority;

@Data
public class CreateTaskRequest {

    @NotBlank(message = "Task title is required")
    private String title;

    private String description;

    private LocalDate dueDate;
    private TaskPriority priority;
    private Integer estimatedHours;
}