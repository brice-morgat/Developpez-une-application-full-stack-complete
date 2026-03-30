package com.openclassrooms.mddapi.service;

import com.openclassrooms.mddapi.dto.AuthRequestDto;
import com.openclassrooms.mddapi.dto.AuthResponseDto;
import com.openclassrooms.mddapi.dto.RegisterRequestDto;
import com.openclassrooms.mddapi.mapper.UserMapper;
import com.openclassrooms.mddapi.model.User;
import com.openclassrooms.mddapi.repository.UserRepository;
import java.time.LocalDateTime;
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
  private final UserMapper userMapper;
  private final UserIdentityNormalizer userIdentityNormalizer;
  private final UserUniquenessValidator userUniquenessValidator;
  private final AuthTokenService authTokenService;

  public AuthService(
      UserRepository userRepository,
      PasswordEncoder passwordEncoder,
      AuthenticationManager authenticationManager,
      UserMapper userMapper,
      UserIdentityNormalizer userIdentityNormalizer,
      UserUniquenessValidator userUniquenessValidator,
      AuthTokenService authTokenService) {
    this.userRepository = userRepository;
    this.passwordEncoder = passwordEncoder;
    this.authenticationManager = authenticationManager;
    this.userMapper = userMapper;
    this.userIdentityNormalizer = userIdentityNormalizer;
    this.userUniquenessValidator = userUniquenessValidator;
    this.authTokenService = authTokenService;
  }

  public AuthResponseDto register(RegisterRequestDto request) {
    String normalizedEmail = userIdentityNormalizer.normalizeEmail(request.email());
    String normalizedUsername = userIdentityNormalizer.normalizeUsername(request.username());

    userUniquenessValidator.ensureEmailAvailable(normalizedEmail);
    userUniquenessValidator.ensureUsernameAvailable(normalizedUsername);

    User user =
        userRepository.save(
            User.builder()
                .email(normalizedEmail)
                .username(normalizedUsername)
                .password(passwordEncoder.encode(request.password()))
                .createdAt(LocalDateTime.now())
                .build());

    String token = authTokenService.generateToken(user);
    return new AuthResponseDto(token, userMapper.toAuthUser(user));
  }

  public AuthResponseDto login(AuthRequestDto request) {
    String identifier = userIdentityNormalizer.normalizeIdentifier(request.identifier());
    String normalizedEmailIdentifier = identifier.toLowerCase();

    User user =
        userRepository
            .findFirstByEmailIgnoreCaseOrUsernameIgnoreCaseOrderByIdDesc(
                normalizedEmailIdentifier, identifier)
            .orElseThrow(() -> new BadCredentialsException("Identifiants invalides."));

    authenticationManager.authenticate(
        new UsernamePasswordAuthenticationToken(user.getUsername(), request.password()));

    String token = authTokenService.generateToken(user);
    return new AuthResponseDto(token, userMapper.toAuthUser(user));
  }
}
