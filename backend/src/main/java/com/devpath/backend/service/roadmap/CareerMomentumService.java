package com.devpath.backend.service.roadmap;

import com.devpath.backend.dto.roadmap.CareerMomentumResponse;
import com.devpath.backend.entity.Roadmap;
import com.devpath.backend.entity.RoadmapTask;
import com.devpath.backend.entity.User;
import com.devpath.backend.repository.RoadmapRepository;
import com.devpath.backend.repository.RoadmapTaskRepository;
import com.devpath.backend.service.auth.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CareerMomentumService {

    private final AuthService authService;
    private final RoadmapRepository roadmapRepository;
    private final RoadmapTaskRepository roadmapTaskRepository;

    public CareerMomentumResponse getCareerMomentum() {

        User currentUser = authService.getCurrentUser();

        List<Roadmap> roadmaps =
                roadmapRepository.findByUser(currentUser);

        if (roadmaps.isEmpty()) {
            return CareerMomentumResponse.builder()
                    .strongestRoadmap("No Roadmaps")
                    .weakestRoadmap("No Roadmaps")
                    .nearestCompletionRoadmap("No Roadmaps")
                    .momentumScore(0)
                    .build();
        }

        Map<Long, List<RoadmapTask>> tasksByRoadmapId =
                roadmapTaskRepository
                        .findAllByRoadmapUserId(currentUser.getId())
                        .stream()
                        .collect(Collectors.groupingBy(
                                task -> task.getRoadmap().getId()
                        ));

        Roadmap strongestRoadmap = null;
        Roadmap weakestRoadmap = null;
        Roadmap nearestCompletionRoadmap = null;

        int strongestProgress = -1;
        int weakestProgress = 101;
        int nearestProgress = -1;

        int totalOverdueTasks = 0;
        int totalCompletedTasks = 0;
        int totalTasks = 0;

        for (Roadmap roadmap : roadmaps) {

            List<RoadmapTask> tasks =
                    tasksByRoadmapId.getOrDefault(
                            roadmap.getId(),
                            List.of()
                    );

            int roadmapTotalTasks = tasks.size();

            int roadmapCompletedTasks = (int) tasks.stream()
                    .filter(RoadmapTask::getCompleted)
                    .count();

            int progress = roadmapTotalTasks == 0
                    ? 0
                    : (roadmapCompletedTasks * 100)
                      / roadmapTotalTasks;

            totalTasks += roadmapTotalTasks;
            totalCompletedTasks += roadmapCompletedTasks;

            int overdue = (int) tasks.stream()
                    .filter(task -> !task.getCompleted())
                    .filter(task -> task.getDueDate() != null)
                    .filter(task ->
                            task.getDueDate()
                                    .isBefore(LocalDate.now())
                    )
                    .count();

            totalOverdueTasks += overdue;

            if (progress > strongestProgress) {
                strongestProgress = progress;
                strongestRoadmap = roadmap;
            }

            if (progress < weakestProgress) {
                weakestProgress = progress;
                weakestRoadmap = roadmap;
            }

            if (progress < 100 && progress > nearestProgress) {
                nearestProgress = progress;
                nearestCompletionRoadmap = roadmap;
            }
        }

        int momentumScore = calculateMomentumScore(
                totalCompletedTasks,
                totalTasks,
                totalOverdueTasks
        );

        return CareerMomentumResponse.builder()
                .strongestRoadmap(
                        strongestRoadmap != null
                                ? strongestRoadmap.getTitle()
                                : null
                )
                .strongestRoadmapProgress(
                        strongestProgress
                )
                .weakestRoadmap(
                        weakestRoadmap != null
                                ? weakestRoadmap.getTitle()
                                : null
                )
                .weakestRoadmapProgress(
                        weakestProgress
                )
                .totalOverdueTasks(
                        totalOverdueTasks
                )
                .nearestCompletionRoadmap(
                        nearestCompletionRoadmap != null
                                ? nearestCompletionRoadmap.getTitle()
                                : null
                )
                .nearestCompletionPercentage(
                        nearestProgress
                )
                .momentumScore(
                        momentumScore
                )
                .build();
    }

    private int calculateMomentumScore(
            int completedTasks,
            int totalTasks,
            int overdueTasks
    ) {

        if (totalTasks == 0) {
            return 0;
        }

        int completionScore =
                (completedTasks * 100) / totalTasks;

        int overduePenalty =
                Math.min(overdueTasks * 5, 30);

        return Math.max(
                completionScore - overduePenalty,
                0
        );
    }
}