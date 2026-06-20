package com.devpath.backend.service.dashboard;

import com.devpath.backend.dto.dashboard.DashboardResponse;
import com.devpath.backend.entity.LeetCodeStats;
import com.devpath.backend.entity.User;
import com.devpath.backend.repository.RoadmapTaskRepository;
import com.devpath.backend.repository.UserRepository;
import com.devpath.backend.service.leetcode.LeetCodeService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class DashboardService {

    private final RoadmapTaskRepository roadmapTaskRepository;
    private final UserRepository userRepository;
    private final LeetCodeService leetCodeService;

    private User getCurrentUser() {
        return (User) SecurityContextHolder
                .getContext()
                .getAuthentication()
                .getPrincipal();
    }

    public DashboardResponse getDashboard() {

        User user = getCurrentUser();

        int totalTasks = Math.toIntExact(
                roadmapTaskRepository.countTotalTasks(user.getId())
        );

        int completedTasks = Math.toIntExact(
                roadmapTaskRepository.countCompletedTasks(user.getId())
        );

        int roadmapProgress = totalTasks == 0
                ? 0
                : (completedTasks * 100) / totalTasks;

        int activeProjects = 0;

        int leetcodeSolved = 0;

        try {
            LeetCodeStats stats = leetCodeService.getStatsForCurrentUser(user);


            if (stats != null) {
                leetcodeSolved = stats.getTotalSolved();
            }

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