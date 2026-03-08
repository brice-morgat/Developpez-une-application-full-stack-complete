package com.openclassrooms.mddapi.controller;

import com.openclassrooms.mddapi.dto.AuthRequestDto;
import com.openclassrooms.mddapi.dto.AuthResponseDto;
import com.openclassrooms.mddapi.dto.RegisterRequestDto;
import com.openclassrooms.mddapi.service.AuthService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

  private final AuthService authService;

  public AuthController(AuthService authService) {
    this.authService = authService;
  }

  @PostMapping("/register")
  public AuthResponseDto register(@Valid @RequestBody RegisterRequestDto request) {
    return authService.register(request);
  }

  @PostMapping("/login")
  public AuthResponseDto login(@Valid @RequestBody AuthRequestDto request) {
    return authService.login(request);
  }
}

