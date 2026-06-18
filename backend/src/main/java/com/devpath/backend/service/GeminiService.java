package com.devpath.backend.service;

import com.google.genai.Client;
import com.google.genai.types.GenerateContentResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class GeminiService {

    private final Client client;
    private final String model;

    public GeminiService(
            @Value("${gemini.api-key}") String apiKey,
            @Value("${gemini.model}") String model
    ) {
        this.client = Client.builder()
                .apiKey(apiKey)
                .build();

        this.model = model;
    }

    public String generate(String prompt) {

        int maxAttempts = 3;

        for (int attempt = 1; attempt <= maxAttempts; attempt++) {

            try {

                GenerateContentResponse response =
                        client.models.generateContent(
                                model,
                                prompt,
                                null
                        );

                return response.text();

            } catch (Exception ex) {

                log.warn(
                        "Gemini failed attempt {}/{}",
                        attempt,
                        maxAttempts,
                        ex
                );

                if (attempt == maxAttempts) {

                    String message = ex.getMessage();

                    if (message != null &&
                            message.contains("429")) {

                        return """
                                {
                                  "strengths": [
                                    "AI insights temporarily unavailable"
                                  ],
                                  "weaknesses": [
                                    "Gemini API quota exceeded"
                                  ],
                                  "nextActions": [
                                    "Try again later"
                                  ]
                                }
                                """;
                    }

                    return """
                            {
                              "strengths": [
                                "AI insights temporarily unavailable"
                              ],
                              "weaknesses": [
                                "AI service unavailable"
                              ],
                              "nextActions": [
                                "Try again later"
                              ]
                            }
                            """;
                }

                try {
                    Thread.sleep(2000L * attempt);
                } catch (InterruptedException ie) {
                    Thread.currentThread().interrupt();
                }
            }
        }

        return """
                {
                  "strengths": [
                    "AI insights temporarily unavailable"
                  ],
                  "weaknesses": [
                    "Unexpected AI service error"
                  ],
                  "nextActions": [
                    "Try again later"
                  ]
                }
                """;
    }
}