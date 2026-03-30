package com.openclassrooms.mddapi.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record CreateCommentRequestDto(
    @NotBlank(message = "Le commentaire est obligatoire.")
    @Size(max = 2000, message = "Le commentaire ne peut pas dépasser 2000 caractères.")
    String content) {}

