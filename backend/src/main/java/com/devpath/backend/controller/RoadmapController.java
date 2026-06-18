package com.devpath.backend.controller;

import com.devpath.backend.dto.CreateRoadmapRequest;
import com.devpath.backend.dto.CreateTaskRequest;
import com.devpath.backend.dto.RoadmapAnalyticsResponse;
import com.devpath.backend.dto.RoadmapResponse;
import com.devpath.backend.dto.RoadmapTaskResponse;
import com.devpath.backend.dto.UpdateRoadmapRequest;
import com.devpath.backend.dto.UpdateTaskRequest;
import com.devpath.backend.service.RoadmapAnalyticsService;
import com.devpath.backend.service.RoadmapService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/roadmaps")
@RequiredArgsConstructor
public class RoadmapController {

    private final RoadmapService roadmapService;
    private final RoadmapAnalyticsService roadmapAnalyticsService;

    @PostMapping
    public RoadmapResponse createRoadmap(
            @Valid @RequestBody CreateRoadmapRequest request
    ) {
        return roadmapService.createRoadmap(request);
    }

    @GetMapping
    public List<RoadmapResponse> getRoadmaps() {
        return roadmapService.getRoadmaps();
    }

    @GetMapping("/{id}")
    public RoadmapResponse getRoadmap(
            @PathVariable Long id
    ) {
        return roadmapService.getRoadmap(id);
    }

    @PutMapping("/{id}")
    public RoadmapResponse updateRoadmap(
            @PathVariable Long id,
            @Valid @RequestBody UpdateRoadmapRequest request
    ) {
        return roadmapService.updateRoadmap(id, request);
    }

    @DeleteMapping("/{id}")
    public void deleteRoadmap(
            @PathVariable Long id
    ) {
        roadmapService.deleteRoadmap(id);
    }

    @PostMapping("/{id}/tasks")
    public RoadmapTaskResponse createTask(
            @PathVariable Long id,
            @Valid @RequestBody CreateTaskRequest request
    ) {
        return roadmapService.createTask(id, request);
    }

    @GetMapping("/{id}/tasks")
    public List<RoadmapTaskResponse> getTasks(
            @PathVariable Long id
    ) {
        return roadmapService.getTasks(id);
    }

    @PutMapping("/tasks/{taskId}")
    public RoadmapTaskResponse updateTask(
            @PathVariable Long taskId,
            @RequestBody UpdateTaskRequest request
    ) {
        return roadmapService.updateTask(taskId, request);
    }

    @DeleteMapping("/tasks/{taskId}")
    public void deleteTask(
            @PathVariable Long taskId
    ) {
        roadmapService.deleteTask(taskId);
    }

    @GetMapping("/{roadmapId}/analytics")
    public ResponseEntity<RoadmapAnalyticsResponse> getRoadmapAnalytics(
            @PathVariable Long roadmapId
    ) {
        return ResponseEntity.ok(
                roadmapAnalyticsService.getRoadmapAnalytics(roadmapId)
        );
    }
}