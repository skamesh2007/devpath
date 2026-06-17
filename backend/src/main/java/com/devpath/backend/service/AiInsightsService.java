package com.devpath.backend.service;

import com.devpath.backend.dto.AIInsightsResponse;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Service;

@Service
public class AiInsightsService {

    private final GeminiService geminiService;
    private final ObjectMapper objectMapper;

    public AiInsightsService(
            GeminiService geminiService,
            ObjectMapper objectMapper
    ) {
        this.geminiService = geminiService;
        this.objectMapper = objectMapper;
    }

    public AIInsightsResponse generateInsights(
            String roadmapSummary,
            int totalTasks,
            int completedTasks
    ) {

        String prompt = """
                Analyze this student's learning progress.

                Roadmaps:
                %s

                Completed Tasks:
                %d

                Total Tasks:
                %d

                Return ONLY valid JSON.

                Format:

                {
                  "strengths": [],
                  "weaknesses": [],
                  "nextActions": []
                }

                No markdown.
                No explanation.
                """
                .formatted(
                        roadmapSummary,
                        completedTasks,
                        totalTasks
                );

        String responseText =
                geminiService.generate(prompt);

        if (responseText == null || responseText.isBlank()) {
            throw new RuntimeException(
                    "Gemini returned empty response"
            );
        }

        try {

            String json = responseText
                    .replace("```json", "")
                    .replace("```", "")
                    .trim();

            return objectMapper.readValue(
                    json,
                    AIInsightsResponse.class
            );

        } catch (Exception e) {

            throw new RuntimeException(
                    "Failed to parse insights response",
                    e
            );
        }
    }
}