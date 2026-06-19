package com.devpath.backend.service.aiinsights;

import com.devpath.backend.dto.aiinsights.AIInsightsResponse;
import com.devpath.backend.entity.AIInsights;
import com.devpath.backend.entity.Roadmap;
import com.devpath.backend.entity.RoadmapTask;
import com.devpath.backend.entity.TaskPriority;
import com.devpath.backend.entity.User;
import com.devpath.backend.exception.ResourceNotFoundException;
import com.devpath.backend.repository.AIInsightsRepository;
import com.devpath.backend.repository.RoadmapRepository;
import com.devpath.backend.repository.RoadmapTaskRepository;
import com.devpath.backend.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class AIInsightsManagerService {

    private final UserRepository userRepository;
    private final RoadmapRepository roadmapRepository;
    private final RoadmapTaskRepository roadmapTaskRepository;
    private final AiInsightsService aiInsightsService;
    private final AIInsightsRepository aiInsightsRepository;

    public AIInsightsResponse getInsights(String username) {

        User user = userRepository
                .findByUsername(username)
                .orElseThrow(() ->
                        new IllegalArgumentException("User not found"));

        List<Roadmap> roadmaps = roadmapRepository.findByUser(user);

        StringBuilder roadmapSummary = new StringBuilder();

        int totalTasks = 0;
        int completedTasks = 0;
        int overdueTasks = 0;
        int highPriorityPendingTasks = 0;
        LocalDate today = LocalDate.now();

        for (Roadmap roadmap : roadmaps) {

            List<RoadmapTask> tasks =
                    roadmapTaskRepository.findByRoadmap(roadmap);

            int roadmapTotal = tasks.size();

            int roadmapCompleted = (int) tasks.stream()
                    .filter(task -> Boolean.TRUE.equals(task.getCompleted()))
                    .count();

            int roadmapOverdue = (int) tasks.stream()
                    .filter(task -> !Boolean.TRUE.equals(task.getCompleted()))
                    .filter(task -> task.getDueDate() != null)
                    .filter(task -> task.getDueDate().isBefore(today))
                    .count();

            int roadmapHighPriorityPending = (int) tasks.stream()
                    .filter(task -> !Boolean.TRUE.equals(task.getCompleted()))
                    .filter(task -> TaskPriority.HIGH.equals(task.getPriority()))
                    .count();

            int progress = roadmapTotal == 0
                    ? 0
                    : (roadmapCompleted * 100) / roadmapTotal;

            roadmapSummary.append(roadmap.getTitle())
                    .append(" - ")
                    .append(progress)
                    .append("%")
                    .append(", completed ")
                    .append(roadmapCompleted)
                    .append("/")
                    .append(roadmapTotal)
                    .append(", overdue ")
                    .append(roadmapOverdue)
                    .append(", high priority pending ")
                    .append(roadmapHighPriorityPending)
                    .append("\n");

            totalTasks += roadmapTotal;
            completedTasks += roadmapCompleted;
            overdueTasks += roadmapOverdue;
            highPriorityPendingTasks += roadmapHighPriorityPending;
        }

        return aiInsightsService.generateInsights(
                user,
                roadmapSummary.toString(),
                totalTasks,
                completedTasks,
                overdueTasks,
                highPriorityPendingTasks
        );
    }

    @Transactional(readOnly = true)
    public AIInsightsResponse getSavedInsights(String username) {

        User user = userRepository.findByUsername(username)
                .orElseThrow(() ->
                        new IllegalArgumentException("User not found"));

        AIInsights aiInsights = aiInsightsRepository.findByUserId(user.getId())
                .orElseThrow(() ->
                        new ResourceNotFoundException("No AI insights found"));

        return AIInsightsResponse.builder()
                .strengths(aiInsights.getStrengths())
                .weaknesses(aiInsights.getWeaknesses())
                .nextActions(aiInsights.getNextActions())
                .build();
    }
}
