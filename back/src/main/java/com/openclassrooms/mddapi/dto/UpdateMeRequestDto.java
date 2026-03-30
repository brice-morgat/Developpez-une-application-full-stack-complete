package com.openclassrooms.mddapi.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Size;

public record UpdateMeRequestDto(
    @Email(message = "Le format de l'e-mail est invalide.") String email,
    @Size(min = 3, max = 100, message = "Le nom d'utilisateur doit contenir entre 3 et 100 caractères.")
    String username,
    @Size(min = 6, max = 120, message = "Le mot de passe doit contenir entre 6 et 120 caractères.")
    String password) {}

