package com.openclassrooms.mddapi.dto;

import java.time.LocalDateTime;

public record PostDto(Long id, String title, String content, Long authorId, Long topicId, LocalDateTime createdAt) {}

