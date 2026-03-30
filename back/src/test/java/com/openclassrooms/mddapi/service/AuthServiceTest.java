package com.openclassrooms.mddapi.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.openclassrooms.mddapi.config.JwtService;
import com.openclassrooms.mddapi.dto.AuthRequestDto;
import com.openclassrooms.mddapi.dto.RegisterRequestDto;
import com.openclassrooms.mddapi.mapper.UserMapper;
import com.openclassrooms.mddapi.model.User;
import com.openclassrooms.mddapi.repository.UserRepository;
import java.time.LocalDateTime;
import java.util.Map;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

  @Mock private UserRepository userRepository;
  @Mock private PasswordEncoder passwordEncoder;
  @Mock private AuthenticationManager authenticationManager;
  @Mock private JwtService jwtService;
  @Mock private UserMapper userMapper;

  @InjectMocks private AuthService authService;

  @Test
  void register_shouldNormalizeAndCreateUser() {
    RegisterRequestDto request = new RegisterRequestDto("  TEST@MAIL.COM  ", "  Alice  ", "secret");
    User savedUser = User.builder().id(7L).username("Alice").email("test@mail.com").password("hashed").createdAt(LocalDateTime.now()).build();

    when(userRepository.existsByEmailIgnoreCase("test@mail.com")).thenReturn(false);
    when(userRepository.existsByUsernameIgnoreCase("Alice")).thenReturn(false);
    when(passwordEncoder.encode("secret")).thenReturn("hashed");
    when(userRepository.save(any(User.class))).thenReturn(savedUser);
    when(jwtService.generateToken(any(), any(Map.class))).thenReturn("token");
    when(userMapper.toAuthUser(savedUser)).thenReturn(new com.openclassrooms.mddapi.dto.AuthUserDto(7L, "test@mail.com", "Alice"));

    var response = authService.register(request);

    assertThat(response.token()).isEqualTo("token");

    ArgumentCaptor<User> captor = ArgumentCaptor.forClass(User.class);
    verify(userRepository).save(captor.capture());
    assertThat(captor.getValue().getEmail()).isEqualTo("test@mail.com");
    assertThat(captor.getValue().getUsername()).isEqualTo("Alice");
  }

  @Test
  void register_shouldFailWhenEmailAlreadyUsed() {
    when(userRepository.existsByEmailIgnoreCase("test@mail.com")).thenReturn(true);

    assertThatThrownBy(() -> authService.register(new RegisterRequestDto("test@mail.com", "alice", "secret")))
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessage("Cet e-mail est déjà utilisé.");
  }

  @Test
  void login_shouldAuthenticateAndReturnToken() {
    AuthRequestDto request = new AuthRequestDto(" Alice ", "secret");
    User existing = User.builder().id(3L).username("Alice").email("alice@mail.com").password("hashed").createdAt(LocalDateTime.now()).build();

    when(userRepository.findFirstByEmailIgnoreCaseOrUsernameIgnoreCaseOrderByIdDesc("alice", "Alice")).thenReturn(Optional.of(existing));
    when(jwtService.generateToken(any(), any(Map.class))).thenReturn("jwt");
    when(userMapper.toAuthUser(existing)).thenReturn(new com.openclassrooms.mddapi.dto.AuthUserDto(3L, "alice@mail.com", "Alice"));

    var response = authService.login(request);

    verify(authenticationManager).authenticate(new UsernamePasswordAuthenticationToken("Alice", "secret"));
    assertThat(response.token()).isEqualTo("jwt");
  }

  @Test
  void login_shouldFailOnUnknownUser() {
    when(userRepository.findFirstByEmailIgnoreCaseOrUsernameIgnoreCaseOrderByIdDesc("unknown", "unknown"))
        .thenReturn(Optional.empty());

    assertThatThrownBy(() -> authService.login(new AuthRequestDto("unknown", "x")))
        .isInstanceOf(BadCredentialsException.class)
        .hasMessage("Identifiants invalides.");
  }
}
