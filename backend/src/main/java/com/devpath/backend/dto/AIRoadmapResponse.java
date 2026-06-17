package com.devpath.backend.dto;

import lombok.Data;

import java.util.List;

@Data
public class AIRoadmapResponse {

    private String roadmapTitle;
    private List<String> tasks;

}