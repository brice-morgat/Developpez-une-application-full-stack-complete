package com.openclassrooms.mddapi.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record CreatePostRequestDto(
    @NotNull(message = "Le thème est obligatoire.") Long topicId,
    @NotBlank(message = "Le titre est obligatoire.")
    @Size(max = 200, message = "Le titre ne peut pas dépasser 200 caractères.")
    String title,
    @NotBlank(message = "Le contenu est obligatoire.")
    @Size(max = 5000, message = "Le contenu ne peut pas dépasser 5000 caractères.")
    String content) {}
