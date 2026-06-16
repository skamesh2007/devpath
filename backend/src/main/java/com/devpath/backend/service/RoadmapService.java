package com.devpath.backend.service;

import com.devpath.backend.dto.ProfileRequest;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.stereotype.Service;

@Service
public class RoadmapService {

    private final ChatClient chatClient;

    public RoadmapService(ChatClient chatClient){
        this.chatClient = chatClient;
    }

    public String generateRoadmap(ProfileRequest request){
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
        return chatClient.prompt()
                .user(prompt)
                .call()
                .content();
    }
}