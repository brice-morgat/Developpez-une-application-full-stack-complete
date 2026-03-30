package com.openclassrooms.mddapi.service;

import com.openclassrooms.mddapi.config.JwtService;
import com.openclassrooms.mddapi.dto.AuthRequestDto;
import com.openclassrooms.mddapi.dto.AuthResponseDto;
import com.openclassrooms.mddapi.dto.RegisterRequestDto;
import com.openclassrooms.mddapi.mapper.UserMapper;
import com.openclassrooms.mddapi.model.User;
import com.openclassrooms.mddapi.repository.UserRepository;
import java.time.LocalDateTime;
import java.util.Map;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthService {

  private final UserRepository userRepository;
  private final PasswordEncoder passwordEncoder;
  private final AuthenticationManager authenticationManager;
  private final JwtService jwtService;
  private final UserMapper userMapper;

  public AuthService(
      UserRepository userRepository,
      PasswordEncoder passwordEncoder,
      AuthenticationManager authenticationManager,
      JwtService jwtService,
      UserMapper userMapper) {
    this.userRepository = userRepository;
    this.passwordEncoder = passwordEncoder;
    this.authenticationManager = authenticationManager;
    this.jwtService = jwtService;
    this.userMapper = userMapper;
  }

  public AuthResponseDto register(RegisterRequestDto request) {
    String normalizedEmail = normalizeEmail(request.email());
    String normalizedUsername = normalizeUsername(request.username());

    if (userRepository.existsByEmailIgnoreCase(normalizedEmail)) {
      throw new IllegalArgumentException("Cet e-mail est déjà utilisé.");
    }
    if (userRepository.existsByUsernameIgnoreCase(normalizedUsername)) {
      throw new IllegalArgumentException("Ce nom d'utilisateur est déjà utilisé.");
    }

    User user =
        userRepository.save(
            User.builder()
                .email(normalizedEmail)
                .username(normalizedUsername)
                .password(passwordEncoder.encode(request.password()))
                .createdAt(LocalDateTime.now())
                .build());

    String token = jwtService.generateToken(org.springframework.security.core.userdetails.User
        .withUsername(user.getUsername()).password(user.getPassword()).authorities("ROLE_USER").build(),
        Map.of("userId", user.getId()));

    return new AuthResponseDto(token, userMapper.toAuthUser(user));
  }

  public AuthResponseDto login(AuthRequestDto request) {
    String identifier = normalizeIdentifier(request.identifier());
    String normalizedEmailIdentifier = identifier.toLowerCase();

    User user =
        userRepository
            .findFirstByEmailIgnoreCaseOrUsernameIgnoreCaseOrderByIdDesc(normalizedEmailIdentifier, identifier)
            .orElseThrow(() -> new BadCredentialsException("Identifiants invalides."));

    authenticationManager.authenticate(
        new UsernamePasswordAuthenticationToken(user.getUsername(), request.password()));

    String token = jwtService.generateToken(org.springframework.security.core.userdetails.User
        .withUsername(user.getUsername()).password(user.getPassword()).authorities("ROLE_USER").build(),
        Map.of("userId", user.getId()));

    return new AuthResponseDto(token, userMapper.toAuthUser(user));
  }

  private String normalizeEmail(String email) {
    return email.trim().toLowerCase();
  }

  private String normalizeUsername(String username) {
    return username.trim();
  }

  private String normalizeIdentifier(String identifier) {
    return identifier.trim();
  }
}

