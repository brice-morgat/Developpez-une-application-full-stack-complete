package com.openclassrooms.mddapi.dto;

import java.time.LocalDateTime;

public record CommentDto(Long id, String content, PostAuthorDto author, LocalDateTime createdAt) {}

