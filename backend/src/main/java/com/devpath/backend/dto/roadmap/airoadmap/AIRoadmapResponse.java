package com.devpath.backend.dto.roadmap.airoadmap;

import lombok.*;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AIRoadmapResponse {

    private String roadmapTitle;

    private List<AIRoadmapTask> tasks;
}