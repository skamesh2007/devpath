package com.devpath.backend.controller;

import com.devpath.backend.dto.AIInsightsResponse;
import com.devpath.backend.service.AIInsightsManagerService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/ai")
@RequiredArgsConstructor
public class AiInsightsController {

    private final AIInsightsManagerService
            aiInsightsManagerService;

    @GetMapping("/insights")
    public AIInsightsResponse getInsights(
            Authentication authentication
    ) {

        return aiInsightsManagerService
                .getInsights(
                        authentication.getName()
                );
    }
}