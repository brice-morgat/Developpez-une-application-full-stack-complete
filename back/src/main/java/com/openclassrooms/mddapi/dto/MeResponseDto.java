package com.openclassrooms.mddapi.dto;

import java.util.List;

public record MeResponseDto(Long id, String email, String username, List<Long> subscriptions) {}

