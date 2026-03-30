package com.openclassrooms.mddapi.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

import com.openclassrooms.mddapi.model.User;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class CurrentUserServiceTest {

  @Mock private CurrentAuthenticatedUserProvider currentAuthenticatedUserProvider;

  @InjectMocks private CurrentUserService currentUserService;

  @Test
  void getCurrentUser_shouldDelegateToProvider() {
    User user = User.builder().id(1L).username("alice").build();
    when(currentAuthenticatedUserProvider.getCurrentUser()).thenReturn(user);

    User result = currentUserService.getCurrentUser();

    assertThat(result.getId()).isEqualTo(1L);
  }
}
