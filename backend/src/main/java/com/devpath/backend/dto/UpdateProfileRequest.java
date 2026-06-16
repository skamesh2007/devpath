package com.devpath.backend.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class UpdateProfileRequest {

    @NotBlank(message = "Username must not be blank")
    @Size(min = 3, max = 30, message = "Username must be between 3 and 30 characters")
    private String username;

    // name is optional — null/blank means "clear it"
    @Size(max = 60, message = "Name must be at most 60 characters")
    private String name;

    @Size(max = 120)
    private String bio;
}