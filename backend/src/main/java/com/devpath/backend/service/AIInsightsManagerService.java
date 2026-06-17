package com.devpath.backend.service;

import com.devpath.backend.dto.AIInsightsResponse;
import com.devpath.backend.entity.Roadmap;
import com.devpath.backend.entity.RoadmapTask;
import com.devpath.backend.entity.User;
import com.devpath.backend.repository.RoadmapRepository;
import com.devpath.backend.repository.RoadmapTaskRepository;
import com.devpath.backend.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AIInsightsManagerService {

    private final UserRepository userRepository;
    private final RoadmapRepository roadmapRepository;
    private final RoadmapTaskRepository roadmapTaskRepository;
    private final AiInsightsService aiInsightsService;

    public AIInsightsResponse getInsights(
            String username
    ) {

        User user = userRepository
                .findByUsername(username)
                .orElseThrow(() ->
                        new IllegalArgumentException(
                                "User not found"
                        ));

        List<Roadmap> roadmaps =
                roadmapRepository.findByUser(user);

        StringBuilder roadmapSummary =
                new StringBuilder();

        int totalTasks = 0;
        int completedTasks = 0;

        for (Roadmap roadmap : roadmaps) {

            List<RoadmapTask> tasks =
                    roadmapTaskRepository
                            .findByRoadmap(roadmap);

            int roadmapTotal =
                    tasks.size();

            int roadmapCompleted =
                    (int) tasks.stream()
                            .filter(RoadmapTask::getCompleted)
                            .count();

            int progress =
                    roadmapTotal == 0
                            ? 0
                            : (roadmapCompleted * 100)
                              / roadmapTotal;

            roadmapSummary.append(
                            roadmap.getTitle()
                    ).append(" - ")
                    .append(progress)
                    .append("%")
                    .append("\n");

            totalTasks += roadmapTotal;
            completedTasks += roadmapCompleted;
        }

        return aiInsightsService.generateInsights(
                roadmapSummary.toString(),
                totalTasks,
                completedTasks
        );
    }
}