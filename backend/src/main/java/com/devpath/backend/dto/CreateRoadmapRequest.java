package com.devpath.backend.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class CreateRoadmapRequest {

    @NotBlank(message = "Title is required")
    private String title;

    private String description;
}