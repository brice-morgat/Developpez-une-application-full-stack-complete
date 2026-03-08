package com.openclassrooms.mddapi.service;

import com.openclassrooms.mddapi.dto.MeResponseDto;
import com.openclassrooms.mddapi.dto.UpdateMeRequestDto;
import com.openclassrooms.mddapi.mapper.UserMapper;
import com.openclassrooms.mddapi.model.User;
import com.openclassrooms.mddapi.repository.SubscriptionRepository;
import com.openclassrooms.mddapi.repository.UserRepository;
import java.util.List;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UserService {

  private final CurrentUserService currentUserService;
  private final SubscriptionRepository subscriptionRepository;
  private final UserRepository userRepository;
  private final PasswordEncoder passwordEncoder;
  private final UserMapper userMapper;

  public UserService(
      CurrentUserService currentUserService,
      SubscriptionRepository subscriptionRepository,
      UserRepository userRepository,
      PasswordEncoder passwordEncoder,
      UserMapper userMapper) {
    this.currentUserService = currentUserService;
    this.subscriptionRepository = subscriptionRepository;
    this.userRepository = userRepository;
    this.passwordEncoder = passwordEncoder;
    this.userMapper = userMapper;
  }

  public MeResponseDto getCurrentUserProfile() {
    User currentUser = currentUserService.getCurrentUser();
    List<Long> topicIds =
        subscriptionRepository.findAllByUserId(currentUser.getId()).stream()
            .map(subscription -> subscription.getTopic().getId())
            .toList();
    return userMapper.toMe(currentUser, topicIds);
  }

  public MeResponseDto updateCurrentUser(UpdateMeRequestDto request) {
    User currentUser = currentUserService.getCurrentUser();

    if (request.email() != null && !request.email().isBlank()) {
      String normalizedEmail = request.email().trim().toLowerCase();
      userRepository
          .findByEmail(normalizedEmail)
          .filter(existing -> !existing.getId().equals(currentUser.getId()))
          .ifPresent(existing -> {
            throw new IllegalArgumentException("Email is already used");
          });
      currentUser.setEmail(normalizedEmail);
    }

    if (request.username() != null && !request.username().isBlank()) {
      String normalizedUsername = request.username().trim();
      userRepository
          .findByUsername(normalizedUsername)
          .filter(existing -> !existing.getId().equals(currentUser.getId()))
          .ifPresent(existing -> {
            throw new IllegalArgumentException("Username is already used");
          });
      currentUser.setUsername(normalizedUsername);
    }

    if (request.password() != null && !request.password().isBlank()) {
      currentUser.setPassword(passwordEncoder.encode(request.password()));
    }

    User updated = userRepository.save(currentUser);
    List<Long> topicIds =
        subscriptionRepository.findAllByUserId(updated.getId()).stream()
            .map(subscription -> subscription.getTopic().getId())
            .toList();
    return userMapper.toMe(updated, topicIds);
  }
}

