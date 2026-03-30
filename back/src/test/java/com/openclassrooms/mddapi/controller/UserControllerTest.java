package com.openclassrooms.mddapi.controller;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.openclassrooms.mddapi.dto.MeResponseDto;
import com.openclassrooms.mddapi.dto.UpdateMeRequestDto;
import com.openclassrooms.mddapi.service.UserService;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class UserControllerTest {

  @Mock private UserService userService;

  @InjectMocks private UserController userController;

  @Test
  void me_shouldReturnServiceValue() {
    MeResponseDto response = new MeResponseDto(1L, "mail@test.com", "alice", List.of(1L));
    when(userService.getCurrentUserProfile()).thenReturn(response);

    MeResponseDto result = userController.me();

    verify(userService).getCurrentUserProfile();
    assertThat(result).isEqualTo(response);
  }

  @Test
  void updateMe_shouldDelegateToService() {
    UpdateMeRequestDto request = new UpdateMeRequestDto("mail2@test.com", "alice2", "secret123");
    MeResponseDto response = new MeResponseDto(1L, "mail2@test.com", "alice2", List.of());
    when(userService.updateCurrentUser(request)).thenReturn(response);

    MeResponseDto result = userController.updateMe(request);

    verify(userService).updateCurrentUser(request);
    assertThat(result).isEqualTo(response);
  }
}
