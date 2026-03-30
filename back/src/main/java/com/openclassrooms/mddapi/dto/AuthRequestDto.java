package com.openclassrooms.mddapi.dto;

import jakarta.validation.constraints.NotBlank;

public record AuthRequestDto(
    @NotBlank(message = "L'identifiant est obligatoire.") String identifier,
    @NotBlank(message = "Le mot de passe est obligatoire.") String password) {}

