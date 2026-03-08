package com.openclassrooms.mddapi.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record RegisterRequestDto(
    @Email @NotBlank String email,
    @NotBlank @Size(min = 3, max = 100) String username,
    @NotBlank @Size(min = 6, max = 120) String password) {}

