package com.openclassrooms.mddapi.dto;

import jakarta.validation.constraints.NotBlank;

public record AuthRequestDto(@NotBlank String identifier, @NotBlank String password) {}

