package com.devpath.backend.controller;

import com.devpath.backend.dto.ProfileRequest;
import com.devpath.backend.service.AiRoadmapService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/ai/roadmaps")
@RequiredArgsConstructor
public class AiRoadmapController {

    private final AiRoadmapService aiRoadmapService;

    @PostMapping("/generate")
    public String generateRoadmap(@RequestBody ProfileRequest request) {
        return aiRoadmapService.generateRoadmap(request);
    }
}