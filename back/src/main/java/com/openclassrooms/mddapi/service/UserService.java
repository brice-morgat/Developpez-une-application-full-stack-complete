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
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
public class UserService {

  private final CurrentAuthenticatedUserProvider currentAuthenticatedUserProvider;
  private final SubscriptionRepository subscriptionRepository;
  private final UserRepository userRepository;
  private final PasswordEncoder passwordEncoder;
  private final UserMapper userMapper;
  private final UserIdentityNormalizer userIdentityNormalizer;
  private final UserUniquenessValidator userUniquenessValidator;

  public UserService(
      CurrentAuthenticatedUserProvider currentAuthenticatedUserProvider,
      SubscriptionRepository subscriptionRepository,
      UserRepository userRepository,
      PasswordEncoder passwordEncoder,
      UserMapper userMapper,
      UserIdentityNormalizer userIdentityNormalizer,
      UserUniquenessValidator userUniquenessValidator) {
    this.currentAuthenticatedUserProvider = currentAuthenticatedUserProvider;
    this.subscriptionRepository = subscriptionRepository;
    this.userRepository = userRepository;
    this.passwordEncoder = passwordEncoder;
    this.userMapper = userMapper;
    this.userIdentityNormalizer = userIdentityNormalizer;
    this.userUniquenessValidator = userUniquenessValidator;
  }

  public MeResponseDto getCurrentUserProfile() {
    User currentUser = currentAuthenticatedUserProvider.getCurrentUser();
    List<Long> topicIds = subscriptionRepository.findTopicIdsByUserId(currentUser.getId());
    return userMapper.toMe(currentUser, topicIds);
  }

  @Transactional
  public MeResponseDto updateCurrentUser(UpdateMeRequestDto request) {
    User currentUser = currentAuthenticatedUserProvider.getCurrentUser();

    if (request.email() != null && !request.email().isBlank()) {
      String normalizedEmail = userIdentityNormalizer.normalizeEmail(request.email());
      userUniquenessValidator.ensureEmailAvailableForUpdate(normalizedEmail, currentUser.getId());
      currentUser.setEmail(normalizedEmail);
    }

    if (request.username() != null && !request.username().isBlank()) {
      String normalizedUsername = userIdentityNormalizer.normalizeUsername(request.username());
      userUniquenessValidator.ensureUsernameAvailableForUpdate(
          normalizedUsername, currentUser.getId());
      currentUser.setUsername(normalizedUsername);
    }

    if (request.password() != null && !request.password().isBlank()) {
      currentUser.setPassword(passwordEncoder.encode(request.password()));
    }

    User updated = userRepository.save(currentUser);
    List<Long> topicIds = subscriptionRepository.findTopicIdsByUserId(updated.getId());
    return userMapper.toMe(updated, topicIds);
  }
}
