package com.devpath.backend.dto.aiinsights;

import lombok.*;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AIInsightsResponse {

    private List<String> strengths;
    private List<String> weaknesses;
    private List<String> nextActions;
}