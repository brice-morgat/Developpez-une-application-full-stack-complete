package com.openclassrooms.mddapi.dto;

import java.time.LocalDateTime;
import java.util.Map;

public record ApiErrorDto(
    LocalDateTime timestamp,
    int status,
    String error,
    String message,
    String path,
    Map<String, String> details) {}

