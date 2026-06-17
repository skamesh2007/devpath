package com.devpath.backend.dto;

import lombok.Data;

import java.util.List;

@Data
public class AIInsightsResponse {

    private List<String> strengths;
    private List<String> weaknesses;
    private List<String> nextActions;

}