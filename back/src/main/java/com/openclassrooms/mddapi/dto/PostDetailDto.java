package com.openclassrooms.mddapi.dto;

import java.time.LocalDateTime;
import java.util.List;

public record PostDetailDto(
    Long id,
    String title,
    String content,
    PostAuthorDto author,
    PostTopicDto topic,
    LocalDateTime createdAt,
    List<CommentDto> comments) {}

