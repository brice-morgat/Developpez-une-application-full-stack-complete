package com.openclassrooms.mddapi.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.openclassrooms.mddapi.dto.AuthRequestDto;
import com.openclassrooms.mddapi.dto.RegisterRequestDto;
import com.openclassrooms.mddapi.mapper.UserMapper;
import com.openclassrooms.mddapi.model.User;
import com.openclassrooms.mddapi.repository.UserRepository;
import java.time.LocalDateTime;
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
  @Mock private UserMapper userMapper;
  @Mock private UserIdentityNormalizer userIdentityNormalizer;
  @Mock private UserUniquenessValidator userUniquenessValidator;
  @Mock private AuthTokenService authTokenService;

  @InjectMocks private AuthService authService;

  @Test
  void register_shouldNormalizeAndCreateUser() {
    RegisterRequestDto request = new RegisterRequestDto("  TEST@MAIL.COM  ", "  Alice  ", "secret");
    User savedUser =
        User.builder()
            .id(7L)
            .username("Alice")
            .email("test@mail.com")
            .password("hashed")
            .createdAt(LocalDateTime.now())
            .build();

    when(userIdentityNormalizer.normalizeEmail("  TEST@MAIL.COM  ")).thenReturn("test@mail.com");
    when(userIdentityNormalizer.normalizeUsername("  Alice  ")).thenReturn("Alice");
    when(passwordEncoder.encode("secret")).thenReturn("hashed");
    when(userRepository.save(any(User.class))).thenReturn(savedUser);
    when(authTokenService.generateToken(savedUser)).thenReturn("token");
    when(userMapper.toAuthUser(savedUser))
        .thenReturn(new com.openclassrooms.mddapi.dto.AuthUserDto(7L, "test@mail.com", "Alice"));

    var response = authService.register(request);

    assertThat(response.token()).isEqualTo("token");

    ArgumentCaptor<User> captor = ArgumentCaptor.forClass(User.class);
    verify(userRepository).save(captor.capture());
    assertThat(captor.getValue().getEmail()).isEqualTo("test@mail.com");
    assertThat(captor.getValue().getUsername()).isEqualTo("Alice");
    verify(userUniquenessValidator).ensureEmailAvailable("test@mail.com");
    verify(userUniquenessValidator).ensureUsernameAvailable("Alice");
  }

  @Test
  void register_shouldFailWhenEmailAlreadyUsed() {
    when(userIdentityNormalizer.normalizeEmail("test@mail.com")).thenReturn("test@mail.com");
    when(userIdentityNormalizer.normalizeUsername("alice")).thenReturn("alice");
    org.mockito.Mockito.doThrow(
            new IllegalArgumentException("Cet e-mail est déjà utilisé."))
        .when(userUniquenessValidator)
        .ensureEmailAvailable("test@mail.com");

    assertThatThrownBy(() -> authService.register(new RegisterRequestDto("test@mail.com", "alice", "secret")))
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessage("Cet e-mail est déjà utilisé.");
  }

  @Test
  void login_shouldAuthenticateAndReturnToken() {
    AuthRequestDto request = new AuthRequestDto(" Alice ", "secret");
    User existing =
        User.builder()
            .id(3L)
            .username("Alice")
            .email("alice@mail.com")
            .password("hashed")
            .createdAt(LocalDateTime.now())
            .build();

    when(userIdentityNormalizer.normalizeIdentifier(" Alice ")).thenReturn("Alice");
    when(userRepository.findFirstByEmailIgnoreCaseOrUsernameIgnoreCaseOrderByIdDesc("alice", "Alice"))
        .thenReturn(Optional.of(existing));
    when(authTokenService.generateToken(existing)).thenReturn("jwt");
    when(userMapper.toAuthUser(existing))
        .thenReturn(new com.openclassrooms.mddapi.dto.AuthUserDto(3L, "alice@mail.com", "Alice"));

    var response = authService.login(request);

    verify(authenticationManager)
        .authenticate(new UsernamePasswordAuthenticationToken("Alice", "secret"));
    assertThat(response.token()).isEqualTo("jwt");
  }

  @Test
  void login_shouldFailOnUnknownUser() {
    when(userIdentityNormalizer.normalizeIdentifier("unknown")).thenReturn("unknown");
    when(userRepository.findFirstByEmailIgnoreCaseOrUsernameIgnoreCaseOrderByIdDesc("unknown", "unknown"))
        .thenReturn(Optional.empty());

    assertThatThrownBy(() -> authService.login(new AuthRequestDto("unknown", "x")))
        .isInstanceOf(BadCredentialsException.class)
        .hasMessage("Identifiants invalides.");
  }
}
