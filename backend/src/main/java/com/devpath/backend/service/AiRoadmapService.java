package com.devpath.backend.service;

import com.devpath.backend.dto.ProfileRequest;
import com.google.genai.Client;
import com.google.genai.types.GenerateContentResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class AiRoadmapService {

    private final Client client;
    private final String model;

    public AiRoadmapService(
            @Value("${gemini.api-key}") String apiKey,
            @Value("${gemini.model}") String model
    ) {
        this.client = Client.builder()
                .apiKey(apiKey)
                .build();

        this.model = model;
    }

    public String generateRoadmap(ProfileRequest request) {

        String prompt = """
You are DevPath AI.

Student Profile:
Year: %d
Branch: %s
Goal: %s
Skills: %s

Generate recommendations.

Return ONLY valid JSON.

Example format:

{
  "skillsToLearn": ["Git", "SQL"],
  "projectsToBuild": ["Student Management System"],
  "placementPreparation": ["Solve 30 Array Problems"]
}

Do not return empty arrays.
Populate every field.
"""
                .formatted(
                        request.getYear(),
                        request.getBranch(),
                        request.getGoal(),
                        request.getSkills()
                );

        GenerateContentResponse response =
                client.models.generateContent(model, prompt, null);

        return response.text();
    }
}