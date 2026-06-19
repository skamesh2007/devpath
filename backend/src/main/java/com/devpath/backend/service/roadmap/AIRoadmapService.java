package com.devpath.backend.service.roadmap;

import com.devpath.backend.dto.roadmap.airoadmap.AIRoadmapResponse;
import com.devpath.backend.dto.roadmap.airoadmap.AIRoadmapTask;
import com.devpath.backend.entity.TaskPriority;
import com.devpath.backend.exception.AiProviderException;
import com.devpath.backend.exception.AiResponseParsingException;
import com.devpath.backend.service.ai.AIJsonResponseParser;
import com.devpath.backend.service.ai.AITextGenerationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class AIRoadmapService {

    private final AITextGenerationService aiTextGenerationService;
    private final AIJsonResponseParser aiJsonResponseParser;

    public AIRoadmapResponse generateRoadmap(String goal) {
        String prompt = buildPrompt(goal);

        try {

            long start = System.currentTimeMillis();
            String responseText = aiTextGenerationService.generate(prompt);
            log.info(
                    "Pure AI time: {} ms",
                    System.currentTimeMillis() - start
            );
            return aiJsonResponseParser.parseObject(
                    responseText,
                    AIRoadmapResponse.class
            );

        } catch (AiProviderException | AiResponseParsingException ex) {
            return fallbackRoadmap(goal);
        }
    }

    private String buildPrompt(String goal) {
        return """
            You are an expert software learning roadmap planner.
            
            Return ONLY valid JSON.
            
            Goal: %s
            
            Current date: %s
            
            JSON schema:
            {
              "roadmapTitle": "string",
              "tasks": [
                {
                  "title": "string",
                  "priority": "HIGH|MEDIUM|LOW",
                  "estimatedHours": number,
                  "dueInDays": number
                }
              ]
            }
            
            Rules:
            - Generate 5 to 10 learning tasks.
            - Tasks must be ordered from beginner to advanced.
            - priority must be HIGH, MEDIUM, or LOW.
            - estimatedHours must be between 2 and 50.
            - dueInDays must be realistic.
            - Earlier foundational tasks should have shorter dueInDays.
            - Advanced tasks should have longer dueInDays.
            - Do not include explanations.
            - Do not include markdown.
            - Return valid JSON only.
            """
                .formatted(goal, LocalDate.now());
    }

    private AIRoadmapResponse fallbackRoadmap(String goal) {
        return AIRoadmapResponse.builder()
                .roadmapTitle(goal)
                .tasks(List.of(
                        AIRoadmapTask.builder()
                                .title("Define the learning goal clearly")
                                .priority(TaskPriority.HIGH)
                                .estimatedHours(2)
                                .dueInDays(1)
                                .build(),
                        AIRoadmapTask.builder()
                                .title("Learn the required fundamentals")
                                .priority(TaskPriority.HIGH)
                                .estimatedHours(10)
                                .dueInDays(7)
                                .build(),
                        AIRoadmapTask.builder()
                                .title("Build a small practice project")
                                .priority(TaskPriority.MEDIUM)
                                .estimatedHours(12)
                                .dueInDays(14)
                                .build(),
                        AIRoadmapTask.builder()
                                .title("Review common mistakes and best practices")
                                .priority(TaskPriority.MEDIUM)
                                .estimatedHours(5)
                                .dueInDays(21)
                                .build(),
                        AIRoadmapTask.builder()
                                .title("Create a final portfolio-ready project")
                                .priority(TaskPriority.LOW)
                                .estimatedHours(20)
                                .dueInDays(30)
                                .build()
                ))
                .build();
    }
}
