package com.openclassrooms.mddapi.service;

import com.openclassrooms.mddapi.config.JwtService;
import com.openclassrooms.mddapi.dto.AuthRequestDto;
import com.openclassrooms.mddapi.dto.AuthResponseDto;
import com.openclassrooms.mddapi.dto.RegisterRequestDto;
import com.openclassrooms.mddapi.exception.ResourceNotFoundException;
import com.openclassrooms.mddapi.mapper.UserMapper;
import com.openclassrooms.mddapi.model.User;
import com.openclassrooms.mddapi.repository.UserRepository;
import java.time.LocalDateTime;
import java.util.Map;
import org.springframework.security.authentication.AuthenticationManager;
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
    if (userRepository.existsByEmail(request.email())) {
      throw new IllegalArgumentException("Email is already used");
    }
    if (userRepository.existsByUsername(request.username())) {
      throw new IllegalArgumentException("Username is already used");
    }

    User user =
        userRepository.save(
            User.builder()
                .email(request.email().trim().toLowerCase())
                .username(request.username().trim())
                .password(passwordEncoder.encode(request.password()))
                .createdAt(LocalDateTime.now())
                .build());

    String token = jwtService.generateToken(org.springframework.security.core.userdetails.User
        .withUsername(user.getUsername()).password(user.getPassword()).authorities("ROLE_USER").build(),
        Map.of("userId", user.getId()));

    return new AuthResponseDto(token, userMapper.toAuthUser(user));
  }

  public AuthResponseDto login(AuthRequestDto request) {
    User user =
        userRepository
            .findByEmailOrUsername(request.identifier(), request.identifier())
            .orElseThrow(() -> new ResourceNotFoundException("Invalid credentials"));

    authenticationManager.authenticate(
        new UsernamePasswordAuthenticationToken(user.getUsername(), request.password()));

    String token = jwtService.generateToken(org.springframework.security.core.userdetails.User
        .withUsername(user.getUsername()).password(user.getPassword()).authorities("ROLE_USER").build(),
        Map.of("userId", user.getId()));

    return new AuthResponseDto(token, userMapper.toAuthUser(user));
  }
}

