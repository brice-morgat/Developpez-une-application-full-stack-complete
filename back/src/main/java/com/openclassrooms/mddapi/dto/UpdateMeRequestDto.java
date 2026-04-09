package com.openclassrooms.mddapi.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record UpdateMeRequestDto(
    @Email(message = "Le format de l'e-mail est invalide.") String email,
    @Size(min = 3, max = 100, message = "Le nom d'utilisateur doit contenir entre 3 et 100 caractères.")
    String username,
    @Size(min = 8, max = 120, message = "Le mot de passe doit contenir entre 8 et 120 caractères.")
    @Pattern(
        regexp = "^$|^(?=.*\\d)(?=.*[a-z])(?=.*[A-Z])(?=.*[^A-Za-z0-9]).{8,120}$",
        message =
            "Le mot de passe doit contenir au moins une minuscule, une majuscule, un chiffre et un caractère spécial.")
    String password) {}
