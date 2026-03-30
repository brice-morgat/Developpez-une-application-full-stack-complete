package com.openclassrooms.mddapi.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record RegisterRequestDto(
    @Email(message = "Le format de l'e-mail est invalide.")
    @NotBlank(message = "L'e-mail est obligatoire.")
    String email,
    @NotBlank(message = "Le nom d'utilisateur est obligatoire.")
    @Size(min = 3, max = 100, message = "Le nom d'utilisateur doit contenir entre 3 et 100 caractères.")
    String username,
    @NotBlank(message = "Le mot de passe est obligatoire.")
    @Size(min = 6, max = 120, message = "Le mot de passe doit contenir entre 6 et 120 caractères.")
    String password) {}

