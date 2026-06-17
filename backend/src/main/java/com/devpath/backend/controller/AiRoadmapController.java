package com.devpath.backend.controller;

import com.devpath.backend.dto.GenerateRoadmapRequest;
import com.devpath.backend.dto.RoadmapResponse;
import com.devpath.backend.service.AIRoadmapManagerService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/ai/roadmaps")
@RequiredArgsConstructor
public class AiRoadmapController {

    private final AIRoadmapManagerService aiRoadmapManagerService;

    @PostMapping("/generate")
    public RoadmapResponse generateRoadmap(
            @RequestBody GenerateRoadmapRequest request,
            Authentication authentication
    ) {

        return aiRoadmapManagerService
                .generateAndSaveRoadmap(
                        request.getGoal(),
                        authentication.getName()
                );
    }
}