package com.devpath.backend.service.ai;

import org.springframework.stereotype.Service;

@Service
public class AIChatService {

    private final AITextGenerationService aiTextGenerationService;

    public AIChatService(AITextGenerationService aiTextGenerationService) {
        this.aiTextGenerationService = aiTextGenerationService;
    }

    public String ask(String question) {
        if (question == null || question.isBlank()) {
            throw new IllegalArgumentException("Question must not be empty");
        }
        return aiTextGenerationService.generate(question);
    }
}