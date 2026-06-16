package com.devpath.backend.controller;

import com.devpath.backend.dto.ProfileRequest;
import com.devpath.backend.service.RoadmapService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/ai")
public class RoadmapController {

    private final RoadmapService roadmapService;

    public RoadmapController(RoadmapService roadmapService) {
        this.roadmapService = roadmapService;
    }

    @PostMapping("/roadmap")
    public String generateRoadmap(@RequestBody ProfileRequest request) {
        return roadmapService.generateRoadmap(request);
    }
}