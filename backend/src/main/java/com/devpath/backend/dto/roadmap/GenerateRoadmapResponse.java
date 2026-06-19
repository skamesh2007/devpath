package com.devpath.backend.dto.roadmap;

import lombok.*;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class GenerateRoadmapResponse {

    private Long roadmapId;
    private String roadmapTitle;

}