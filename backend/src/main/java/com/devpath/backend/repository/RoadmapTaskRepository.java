package com.devpath.backend.repository;

import com.devpath.backend.entity.Roadmap;
import com.devpath.backend.entity.RoadmapTask;
import com.devpath.backend.entity.TaskPriority;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDate;
import java.util.List;

public interface RoadmapTaskRepository extends JpaRepository<RoadmapTask, Long> {

    List<RoadmapTask> findByRoadmap(Roadmap roadmap);

    List<RoadmapTask> findByRoadmap_Id(Long roadmapId);

    long countByRoadmap_IdAndCompletedTrue(Long roadmapId);

    long countByRoadmap_IdAndCompletedFalse(Long roadmapId);

    long countByRoadmap_IdAndCompletedFalseAndPriority(
            Long roadmapId,
            TaskPriority priority
    );

    long countByRoadmap_IdAndCompletedFalseAndDueDateBefore(
            Long roadmapId,
            LocalDate date
    );

    List<RoadmapTask> findByRoadmap_IdAndCompletedFalse(Long roadmapId);

    @Query("""
        SELECT COUNT(t)
        FROM RoadmapTask t 
        WHERE t.roadmap.user.id = :userId
""")
    long countTotalTasks(Long userId);

    @Query("""
    SELECT COUNT(t)
    FROM RoadmapTask t
    WHERE t.roadmap.user.id = :userId
      AND t.completed = true
""")
    long countCompletedTasks(Long userId);
    @Query("""
    SELECT COUNT(t)
    FROM RoadmapTask t
    WHERE t.roadmap.user.id = :userId
      AND t.completed = false
""")
    long countPendingTasks(Long userId);
}