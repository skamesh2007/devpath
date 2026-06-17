package com.devpath.backend.repository;

import com.devpath.backend.entity.Roadmap;
import com.devpath.backend.entity.RoadmapTask;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface RoadmapTaskRepository extends JpaRepository<RoadmapTask, Long> {

    List<RoadmapTask> findByRoadmap(Roadmap roadmap);
}