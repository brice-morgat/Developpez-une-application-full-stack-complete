package com.openclassrooms.mddapi.controller;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.openclassrooms.mddapi.dto.AuthRequestDto;
import com.openclassrooms.mddapi.dto.AuthResponseDto;
import com.openclassrooms.mddapi.dto.AuthUserDto;
import com.openclassrooms.mddapi.dto.RegisterRequestDto;
import com.openclassrooms.mddapi.service.AuthService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class AuthControllerTest {

  @Mock private AuthService authService;

  @InjectMocks private AuthController authController;

  @Test
  void register_shouldDelegateToService() {
    RegisterRequestDto request = new RegisterRequestDto("mail@test.com", "alice", "secret123");
    AuthResponseDto response = new AuthResponseDto("jwt", new AuthUserDto(1L, "mail@test.com", "alice"));

    when(authService.register(request)).thenReturn(response);

    AuthResponseDto result = authController.register(request);

    verify(authService).register(request);
    assertThat(result).isEqualTo(response);
  }

  @Test
  void login_shouldDelegateToService() {
    AuthRequestDto request = new AuthRequestDto("alice", "secret123");
    AuthResponseDto response = new AuthResponseDto("jwt", new AuthUserDto(1L, "mail@test.com", "alice"));

    when(authService.login(request)).thenReturn(response);

    AuthResponseDto result = authController.login(request);

    verify(authService).login(request);
    assertThat(result).isEqualTo(response);
  }
}
