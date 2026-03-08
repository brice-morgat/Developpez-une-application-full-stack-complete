package com.openclassrooms.mddapi.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Size;

public record UpdateMeRequestDto(
    @Email String email, @Size(min = 3, max = 100) String username, @Size(min = 6, max = 120) String password) {}

