package com.devpath.backend.controller.ai;

import com.devpath.backend.dto.roadmap.GenerateRoadmapRequest;
import com.devpath.backend.dto.roadmap.RoadmapResponse;
import com.devpath.backend.service.roadmap.AIRoadmapManagerService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/ai/roadmaps")
@RequiredArgsConstructor
public class AIRoadmapController {

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