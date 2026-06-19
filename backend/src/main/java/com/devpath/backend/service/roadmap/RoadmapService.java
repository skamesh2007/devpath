package com.devpath.backend.service.roadmap;

import com.devpath.backend.dto.roadmap.CreateRoadmapRequest;
import com.devpath.backend.dto.roadmap.task.CreateTaskRequest;
import com.devpath.backend.dto.roadmap.RoadmapResponse;
import com.devpath.backend.dto.roadmap.task.RoadmapTaskResponse;
import com.devpath.backend.dto.roadmap.UpdateRoadmapRequest;
import com.devpath.backend.dto.roadmap.task.UpdateTaskRequest;
import com.devpath.backend.entity.Roadmap;
import com.devpath.backend.entity.RoadmapTask;
import com.devpath.backend.entity.User;
import com.devpath.backend.repository.RoadmapRepository;
import com.devpath.backend.repository.RoadmapTaskRepository;
import com.devpath.backend.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class RoadmapService {

    private final RoadmapRepository roadmapRepository;
    private final RoadmapTaskRepository taskRepository;
    private final UserRepository userRepository;

    private User getCurrentUser() {
        String username = SecurityContextHolder
                .getContext()
                .getAuthentication()
                .getName();

        return userRepository.findByUsername(username)
                .orElseThrow(() ->
                        new IllegalArgumentException("User not found"));
    }

    private Roadmap getOwnedRoadmap(Long roadmapId) {
        User user = getCurrentUser();

        Roadmap roadmap = roadmapRepository.findById(roadmapId)
                .orElseThrow(() ->
                        new IllegalArgumentException("Roadmap not found"));

        if (!roadmap.getUser().getId().equals(user.getId())) {
            throw new IllegalArgumentException("Access denied");
        }

        return roadmap;
    }

    public RoadmapResponse createRoadmap(CreateRoadmapRequest request) {

        User user = getCurrentUser();

        Roadmap roadmap = Roadmap.builder()
                .title(request.getTitle())
                .description(request.getDescription())
                .user(user)
                .build();

        roadmapRepository.save(roadmap);

        return mapRoadmap(roadmap);
    }

    public List<RoadmapResponse> getRoadmaps() {

        User user = getCurrentUser();

        return roadmapRepository.findByUser(user)
                .stream()
                .map(this::mapRoadmap)
                .toList();
    }

    public RoadmapResponse getRoadmap(Long id) {
        return mapRoadmap(getOwnedRoadmap(id));
    }

    public RoadmapResponse updateRoadmap(
            Long id,
            UpdateRoadmapRequest request
    ) {

        Roadmap roadmap = getOwnedRoadmap(id);

        roadmap.setTitle(request.getTitle());
        roadmap.setDescription(request.getDescription());

        roadmapRepository.save(roadmap);

        return mapRoadmap(roadmap);
    }

    public void deleteRoadmap(Long id) {

        Roadmap roadmap = getOwnedRoadmap(id);

        roadmapRepository.delete(roadmap);
    }

    public RoadmapTaskResponse createTask(
            Long roadmapId,
            CreateTaskRequest request
    ) {

        Roadmap roadmap = getOwnedRoadmap(roadmapId);

        RoadmapTask task = RoadmapTask.builder()
                .title(request.getTitle())
                .description(request.getDescription())
                .completed(false)
                .roadmap(roadmap)
                .build();

        taskRepository.save(task);

        return mapTask(task);
    }

    public List<RoadmapTaskResponse> getTasks(Long roadmapId) {

        Roadmap roadmap = getOwnedRoadmap(roadmapId);

        return taskRepository.findByRoadmap(roadmap)
                .stream()
                .map(this::mapTask)
                .toList();
    }

    public RoadmapTaskResponse updateTask(
            Long taskId,
            UpdateTaskRequest request
    ) {

        RoadmapTask task = taskRepository.findById(taskId)
                .orElseThrow(() ->
                        new IllegalArgumentException("Task not found"));

        getOwnedRoadmap(task.getRoadmap().getId());

        if (request.getTitle() != null) {
            task.setTitle(request.getTitle());
        }

        if (request.getDescription() != null) {
            task.setDescription(request.getDescription());
        }

        if (request.getCompleted() != null) {
            task.setCompleted(request.getCompleted());
        }

        taskRepository.save(task);

        return mapTask(task);
    }

    public void deleteTask(Long taskId) {

        RoadmapTask task = taskRepository.findById(taskId)
                .orElseThrow(() ->
                        new IllegalArgumentException("Task not found"));

        getOwnedRoadmap(task.getRoadmap().getId());

        taskRepository.delete(task);
    }

    private RoadmapResponse mapRoadmap(Roadmap roadmap) {

        int totalTasks = roadmap.getTasks().size();

        int completedTasks = (int) roadmap.getTasks()
                .stream()
                .filter(RoadmapTask::getCompleted)
                .count();

        int progress = totalTasks == 0
                ? 0
                : (completedTasks * 100) / totalTasks;

        return RoadmapResponse.builder()
                .id(roadmap.getId())
                .title(roadmap.getTitle())
                .description(roadmap.getDescription())
                .totalTasks(totalTasks)
                .completedTasks(completedTasks)
                .progress(progress)
                .build();
    }

    private RoadmapTaskResponse mapTask(RoadmapTask task) {

        return RoadmapTaskResponse.builder()
                .id(task.getId())
                .title(task.getTitle())
                .description(task.getDescription())
                .completed(task.getCompleted())
                .build();
    }
}