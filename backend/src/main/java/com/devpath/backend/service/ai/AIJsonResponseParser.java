package com.devpath.backend.service.ai;

import com.devpath.backend.exception.AiResponseParsingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class AIJsonResponseParser {

    private final ObjectMapper objectMapper;

    public <T> T parseObject(String responseText, Class<T> targetType) {
        if (responseText == null || responseText.isBlank()) {
            throw new AiResponseParsingException("AI response was empty");
        }

        try {
            String json = extractJsonObject(responseText);
            return objectMapper.readValue(json, targetType);
        } catch (Exception ex) {
            throw new AiResponseParsingException(
                    "Failed to parse AI response as " + targetType.getSimpleName(),
                    ex
            );
        }
    }

    private String extractJsonObject(String text) {
        String cleaned = text
                .replace("```json", "")
                .replace("```", "")
                .trim();

        int start = cleaned.indexOf("{");
        int end = cleaned.lastIndexOf("}");

        if (start == -1 || end == -1 || end <= start) {
            throw new AiResponseParsingException(
                    "No JSON object found in AI response"
            );
        }

        return cleaned.substring(start, end + 1);
    }
}