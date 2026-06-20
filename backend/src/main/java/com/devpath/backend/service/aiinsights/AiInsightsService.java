package com.devpath.backend.service.aiinsights;

import com.devpath.backend.dto.aiinsights.AIInsightsResponse;
import com.devpath.backend.entity.AIInsights;
import com.devpath.backend.entity.User;
import com.devpath.backend.exception.AiProviderException;
import com.devpath.backend.exception.AiResponseParsingException;
import com.devpath.backend.repository.AIInsightsRepository;
import com.devpath.backend.service.ai.AIJsonResponseParser;
import com.devpath.backend.service.ai.AITextGenerationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class AiInsightsService {

    private final AITextGenerationService aiTextGenerationService;
    private final AIJsonResponseParser aiJsonResponseParser;
    private final AIInsightsRepository aiInsightsRepository;

    public AIInsightsResponse generateInsights(
            User user,
            String roadmapSummary,
            int totalTasks,
            int completedTasks,
            int overdueTasks,
            int highPriorityPendingTasks
    ) {
        String prompt = buildPrompt(
                roadmapSummary,
                totalTasks,
                completedTasks,
                overdueTasks,
                highPriorityPendingTasks
        );

        AIInsightsResponse insights;

        try {
            String responseText = aiTextGenerationService.generate(prompt);

            insights = aiJsonResponseParser.parseObject(
                    responseText,
                    AIInsightsResponse.class
            );

            if (!isValidInsights(insights)) {
                insights = fallbackInsights(
                        totalTasks,
                        completedTasks,
                        overdueTasks,
                        highPriorityPendingTasks
                );
            }

        } catch (AiProviderException | AiResponseParsingException ex) {
            insights = fallbackInsights(
                    totalTasks,
                    completedTasks,
                    overdueTasks,
                    highPriorityPendingTasks
            );
        }

        saveInsights(user, insights);

        return insights;
    }

    private String buildPrompt(
            String roadmapSummary,
            int totalTasks,
            int completedTasks,
            int overdueTasks,
            int highPriorityPendingTasks
    ) {
        int pendingTasks = totalTasks - completedTasks;
        int progress = totalTasks == 0 ? 0 : (completedTasks * 100) / totalTasks;

        return """
                Return only valid JSON.
                
                Use only the provided metrics.
                
                A strength must be directly supported by the data.
                A weakness must be directly supported by the data.
                Do not infer motivation, discipline, focus, consistency,
                confidence, personality traits, or causes.
                
                Each array must contain 1-3 short items.
                
                Progress:
                progressPercent=%d
                completedTasks=%d
                pendingTasks=%d
                totalTasks=%d
                overdueTasks=%d
                highPriorityPendingTasks=%d
                
                Roadmaps:
                %s
                
                Output format:
                
                {
                  "strengths": [],
                  "weaknesses": [],
                  "nextActions": []
                }
                
                Examples of valid strengths:
                - "15 tasks completed"
                - "AI Engineer roadmap is 62%% complete"
                
                Examples of valid weaknesses:
                - "36 tasks remaining"
                - "5 high-priority tasks pending"
                
                Examples of valid actions:
                - "Complete a high-priority pending task"
                - "Continue the AI Engineer roadmap"
                
                Return JSON only.
                """.formatted(
                        progress,
                        completedTasks,
                        pendingTasks,
                        totalTasks,
                        overdueTasks,
                        highPriorityPendingTasks,
                        roadmapSummary
                );
    }

    private AIInsightsResponse fallbackInsights(
            int totalTasks,
            int completedTasks,
            int overdueTasks,
            int highPriorityPendingTasks
    ) {
        int pendingTasks = totalTasks - completedTasks;
        int progress = totalTasks == 0 ? 0 : (completedTasks * 100) / totalTasks;

        return AIInsightsResponse.builder()
                .strengths(List.of(
                        completedTasks > 0
                                ? completedTasks + " tasks completed so far"
                                : "Roadmap tasks are ready to be started",
                        "Overall progress is " + progress + "%"
                ))
                .weaknesses(List.of(
                        overdueTasks > 0
                                ? overdueTasks + " pending tasks are overdue"
                                : pendingTasks + " tasks are still pending",
                        highPriorityPendingTasks > 0
                                ? highPriorityPendingTasks + " high priority tasks are pending"
                                : "No high priority pending tasks were found"
                ))
                .nextActions(List.of(
                        overdueTasks > 0
                                ? "Complete the overdue task first"
                                : "Complete the next pending roadmap task",
                        pendingTasks > 0
                                ? "Update task progress after each study session"
                                : "Add more advanced tasks to continue progressing",
                        "Refresh insights after updating your progress"
                ))
                .build();
    }

    private void saveInsights(User user, AIInsightsResponse insights) {
        AIInsights aiInsights = aiInsightsRepository.findByUserId(user.getId())
                .orElseGet(() -> AIInsights.builder()
                        .user(user)
                        .build());

        aiInsights.setStrengths(new ArrayList<>(insights.getStrengths()));
        aiInsights.setWeaknesses(new ArrayList<>(insights.getWeaknesses()));
        aiInsights.setNextActions(new ArrayList<>(insights.getNextActions()));

        log.info(
                "Generated insights: strengths={}, weaknesses={}, nextActions={}",
                insights.getStrengths(),
                insights.getWeaknesses(),
                insights.getNextActions()
        );

        aiInsights.setUser(user);

        aiInsightsRepository.save(aiInsights);
    }

    private boolean isValidInsights(AIInsightsResponse insights) {
        return insights != null
                && insights.getStrengths() != null
                && !insights.getStrengths().isEmpty()
                && insights.getWeaknesses() != null
                && !insights.getWeaknesses().isEmpty()
                && insights.getNextActions() != null
                && !insights.getNextActions().isEmpty();
    }
}
