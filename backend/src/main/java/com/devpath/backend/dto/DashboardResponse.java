package com.devpath.backend.dto;

import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DashboardResponse {

    private int roadmapProgress;
    private int completedTasks;
    private int totalTasks;
    private int activeProjects;
    private int leetcodeSolved;
}