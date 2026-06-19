package com.devpath.backend.service.roadmap;

import com.devpath.backend.dto.roadmap.task.RecommendedTaskResponse;
import com.devpath.backend.dto.roadmap.RoadmapAnalyticsResponse;
import com.devpath.backend.entity.Roadmap;
import com.devpath.backend.entity.RoadmapTask;
import com.devpath.backend.entity.TaskPriority;
import com.devpath.backend.repository.RoadmapRepository;
import com.devpath.backend.repository.RoadmapTaskRepository;
import com.devpath.backend.service.auth.AuthService;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.stereotype.Service;
import com.devpath.backend.entity.User;
import org.springframework.security.access.AccessDeniedException;
import java.time.LocalDate;
import java.util.Comparator;
import java.util.List;

@Service
public class RoadmapAnalyticsService {

    private final RoadmapRepository roadmapRepository;
    private final RoadmapTaskRepository roadmapTaskRepository;
    private final AuthService authService;

    public RoadmapAnalyticsService(
            RoadmapRepository roadmapRepository,
            RoadmapTaskRepository roadmapTaskRepository,
            AuthService authService
    ) {
        this.roadmapRepository = roadmapRepository;
        this.roadmapTaskRepository = roadmapTaskRepository;
        this.authService = authService;
    }

    public RoadmapAnalyticsResponse getRoadmapAnalytics(Long roadmapId) {

        Roadmap roadmap = roadmapRepository.findById(roadmapId)
                .orElseThrow(() -> new EntityNotFoundException("Roadmap not found"));

        User currentUser = authService.getCurrentUser();

        if (!roadmap.getUser().getId().equals(currentUser.getId())) {
            throw new AccessDeniedException("Access denied");
        }

        List<RoadmapTask> tasks =
                roadmapTaskRepository.findByRoadmap_Id(roadmapId);

        int totalTasks = tasks.size();

        int completedTasks = (int) tasks.stream()
                .filter(RoadmapTask::getCompleted)
                .count();

        int remainingTasks = totalTasks - completedTasks;

        int completionPercentage = totalTasks == 0
                ? 0
                : (completedTasks * 100) / totalTasks;

        int overdueTasks = (int) tasks.stream()
                .filter(task -> !task.getCompleted())
                .filter(task -> task.getDueDate() != null)
                .filter(task -> task.getDueDate().isBefore(LocalDate.now()))
                .count();

        int highPriorityRemaining = (int) tasks.stream()
                .filter(task -> !task.getCompleted())
                .filter(task -> task.getPriority() == TaskPriority.HIGH)
                .count();

        int estimatedHoursRemaining = tasks.stream()
                .filter(task -> !task.getCompleted())
                .map(RoadmapTask::getEstimatedHours)
                .filter(hours -> hours != null)
                .mapToInt(Integer::intValue)
                .sum();

        int projectedCompletionDays = -1;

        List<RecommendedTaskResponse> recommendations =
                buildRecommendations(tasks);

        RoadmapAnalyticsResponse response =
                new RoadmapAnalyticsResponse();

        response.setRoadmapId(roadmap.getId());
        response.setRoadmapTitle(roadmap.getTitle());

        response.setCompletionPercentage(completionPercentage);
        response.setCompletedTasks(completedTasks);
        response.setRemainingTasks(remainingTasks);

        response.setOverdueTasks(overdueTasks);
        response.setHighPriorityRemaining(highPriorityRemaining);

        response.setEstimatedHoursRemaining(
                estimatedHoursRemaining
        );

        response.setProjectedCompletionDays(
                projectedCompletionDays
        );

        response.setNextRecommendedTasks(
                recommendations
        );

        return response;
    }

    private List<RecommendedTaskResponse> buildRecommendations(
            List<RoadmapTask> tasks
    ) {

        return tasks.stream()
                .filter(task -> !task.getCompleted())
                .sorted(
                        Comparator
                                .comparing(
                                        (RoadmapTask task) ->
                                                task.getPriority() != TaskPriority.HIGH
                                )
                                .thenComparing(
                                        task -> task.getDueDate() == null
                                                ? LocalDate.MAX
                                                : task.getDueDate()
                                )
                                .thenComparing(
                                        RoadmapTask::getId
                                )
                )
                .limit(3)
                .map(task -> {
                    RecommendedTaskResponse dto =
                            new RecommendedTaskResponse();

                    dto.setTaskId(task.getId());
                    dto.setTitle(task.getTitle());
                    dto.setPriority(task.getPriority());
                    dto.setDueDate(task.getDueDate());
                    dto.setEstimatedHours(
                            task.getEstimatedHours()
                    );

                    return dto;
                })
                .toList();
    }
}