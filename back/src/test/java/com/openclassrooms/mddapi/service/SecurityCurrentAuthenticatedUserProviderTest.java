package com.openclassrooms.mddapi.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;

import com.openclassrooms.mddapi.model.User;
import com.openclassrooms.mddapi.repository.UserRepository;
import java.util.Optional;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationCredentialsNotFoundException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;

@ExtendWith(MockitoExtension.class)
class SecurityCurrentAuthenticatedUserProviderTest {

  @Mock private UserRepository userRepository;

  @InjectMocks private SecurityCurrentAuthenticatedUserProvider provider;

  @AfterEach
  void clearContext() {
    SecurityContextHolder.clearContext();
  }

  @Test
  void getCurrentUser_shouldReturnAuthenticatedUser() {
    SecurityContextHolder.getContext()
        .setAuthentication(new UsernamePasswordAuthenticationToken("alice", "n/a"));
    User user = User.builder().id(1L).username("alice").build();
    when(userRepository.findByUsername("alice")).thenReturn(Optional.of(user));

    User result = provider.getCurrentUser();

    assertThat(result.getId()).isEqualTo(1L);
  }

  @Test
  void getCurrentUser_shouldFailWhenAuthenticationMissing() {
    assertThatThrownBy(() -> provider.getCurrentUser())
        .isInstanceOf(AuthenticationCredentialsNotFoundException.class)
        .hasMessage("Utilisateur non authentifié.");
  }
}
