package com.openclassrooms.mddapi.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record CreatePostRequestDto(
    @NotNull Long topicId,
    @NotBlank @Size(max = 200) String title,
    @NotBlank @Size(max = 5000) String content) {}

