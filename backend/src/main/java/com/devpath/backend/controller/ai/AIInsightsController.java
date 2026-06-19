package com.devpath.backend.controller.ai;

import com.devpath.backend.dto.aiinsights.AIInsightsResponse;
import com.devpath.backend.service.aiinsights.AIInsightsManagerService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/ai")
@RequiredArgsConstructor
public class AIInsightsController {

    private final AIInsightsManagerService
            aiInsightsManagerService;

    @PostMapping("/insights")
    public AIInsightsResponse getInsights(
            Authentication authentication
    ) {

        return aiInsightsManagerService
                .getInsights(
                        authentication.getName()
                );
    }

    @GetMapping("/insights")
    public AIInsightsResponse getSavedInsights(Authentication authentication) {
        return aiInsightsManagerService.getSavedInsights(authentication.getName());
    }
}