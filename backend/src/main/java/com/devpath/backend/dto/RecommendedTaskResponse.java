package com.devpath.backend.dto;


import com.devpath.backend.entity.TaskPriority;

import java.time.LocalDate;
import lombok.Data;

@Data
public class RecommendedTaskResponse {

    private Long taskId;
    private String title;
    private TaskPriority priority;
    private LocalDate dueDate;
    private Integer estimatedHours;
}
