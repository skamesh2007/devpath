package com.devpath.backend.service;

import com.devpath.backend.dto.DashboardResponse;
import com.devpath.backend.dto.LeetCodeStatsResponse;
import com.devpath.backend.entity.Roadmap;
import com.devpath.backend.entity.User;
import com.devpath.backend.repository.RoadmapRepository;
import com.devpath.backend.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class DashboardService {

    private final RoadmapRepository roadmapRepository;
    private final UserRepository userRepository;
    private final LeetCodeService leetCodeService;

    private User getCurrentUser() {

        String username = SecurityContextHolder
                .getContext()
                .getAuthentication()
                .getName();

        return userRepository.findByUsername(username)
                .orElseThrow(() ->
                        new IllegalArgumentException("User not found"));
    }

    public DashboardResponse getDashboard() {

        User user = getCurrentUser();

        List<Roadmap> roadmaps = roadmapRepository.findByUser(user);

        int totalTasks = roadmaps.stream()
                .mapToInt(r -> r.getTasks().size())
                .sum();

        int completedTasks = roadmaps.stream()
                .flatMap(r -> r.getTasks().stream())
                .mapToInt(task ->
                        Boolean.TRUE.equals(task.getCompleted()) ? 1 : 0)
                .sum();

        int roadmapProgress = totalTasks == 0
                ? 0
                : (completedTasks * 100) / totalTasks;

        int activeProjects = 0;

        int leetcodeSolved = 0;

        try {
            LeetCodeStatsResponse stats =
                    leetCodeService.getStatsForCurrentUser(
                            user.getUsername()
                    );

            leetcodeSolved = stats.getTotalSolved();

        } catch (Exception ignored) {
            // User may not have linked a LeetCode account yet
        }

        return DashboardResponse.builder()
                .roadmapProgress(roadmapProgress)
                .completedTasks(completedTasks)
                .totalTasks(totalTasks)
                .activeProjects(activeProjects)
                .leetcodeSolved(leetcodeSolved)
                .build();
    }
}