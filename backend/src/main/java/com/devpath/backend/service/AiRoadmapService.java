package com.devpath.backend.service;

import com.devpath.backend.dto.AIRoadmapResponse;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Service;

@Service
public class AiRoadmapService {

    private final GeminiService geminiService;
    private final ObjectMapper objectMapper;

    public AiRoadmapService(
            GeminiService geminiService,
            ObjectMapper objectMapper
    ) {
        this.geminiService = geminiService;
        this.objectMapper = objectMapper;
    }

    public AIRoadmapResponse generateRoadmap(String goal) {

        String prompt = """
                You are a software career roadmap generator.

                Generate a roadmap for:

                Goal: %s

                Return ONLY valid JSON.

                Format:

                {
                  "roadmapTitle": "Java Backend Developer",
                  "tasks": [
                    "Java Fundamentals",
                    "OOP",
                    "Collections",
                    "Exception Handling",
                    "JDBC",
                    "Spring Boot",
                    "JPA",
                    "Spring Security",
                    "Microservices"
                  ]
                }

                Rules:
                - Return only JSON
                - No markdown
                - No explanations
                - At least 8 tasks
                - Tasks must be ordered from beginner to advanced
                """
                .formatted(goal);

        String responseText =
                geminiService.generate(prompt);

        if (responseText == null || responseText.isBlank()) {
            throw new RuntimeException("Gemini returned empty response");
        }

        try {

            String json = responseText
                    .replace("```json", "")
                    .replace("```", "")
                    .trim();

            return objectMapper.readValue(
                    json,
                    AIRoadmapResponse.class
            );

        } catch (Exception e) {

            throw new RuntimeException(
                    "Failed to parse AI response: " + responseText,
                    e
            );
        }
    }
}