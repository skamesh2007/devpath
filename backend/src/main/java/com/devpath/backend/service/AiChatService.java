package com.devpath.backend.service;

import com.google.genai.Client;
import com.google.genai.errors.ServerException;
import com.google.genai.types.GenerateContentResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class AiChatService {

    private final Client client;
    private final String model;

    public AiChatService(@Value("${gemini.api-key}") String apiKey,
                         @Value("${gemini.model}") String model) {
        this.client = Client.builder().apiKey(apiKey).build();
        this.model = model;
    }

    public String ask(String question) {
        if (question == null || question.isBlank()) {
            throw new IllegalArgumentException("Question must not be empty");
        }
        return generateWithRetry(question, 3);
    }

    private String generateWithRetry(String prompt, int attemptsLeft) {
        try {
            GenerateContentResponse response = client.models.generateContent(model, prompt, null);
            return response.text();
        } catch (ServerException ex) {
            if (attemptsLeft > 1) {
                try {
                    Thread.sleep(1500);
                } catch (InterruptedException ignored) {
                    Thread.currentThread().interrupt();
                }
                return generateWithRetry(prompt, attemptsLeft - 1);
            }
            throw ex;
        }
    }
}