package com.openclassrooms.mddapi.dto;

import java.time.LocalDateTime;

public record FeedPostDto(
    Long id,
    String title,
    String content,
    PostAuthorDto author,
    PostTopicDto topic,
    LocalDateTime createdAt) {}

