package com.openclassrooms.mddapi.service;

import com.openclassrooms.mddapi.repository.UserRepository;
import org.springframework.stereotype.Component;

@Component
public class UserUniquenessValidator {

  private final UserRepository userRepository;

  public UserUniquenessValidator(UserRepository userRepository) {
    this.userRepository = userRepository;
  }

  public void ensureEmailAvailable(String email) {
    if (userRepository.existsByEmailIgnoreCase(email)) {
      throw new IllegalArgumentException("Cet e-mail est déjà utilisé.");
    }
  }

  public void ensureUsernameAvailable(String username) {
    if (userRepository.existsByUsernameIgnoreCase(username)) {
      throw new IllegalArgumentException(
          "Ce nom d'utilisateur est déjà utilisé.");
    }
  }

  public void ensureEmailAvailableForUpdate(String email, Long currentUserId) {
    userRepository
        .findByEmailIgnoreCase(email)
        .filter(existing -> !existing.getId().equals(currentUserId))
        .ifPresent(
            existing -> {
              throw new IllegalArgumentException("Cet e-mail est déjà utilisé.");
            });
  }

  public void ensureUsernameAvailableForUpdate(String username, Long currentUserId) {
    userRepository
        .findByUsernameIgnoreCase(username)
        .filter(existing -> !existing.getId().equals(currentUserId))
        .ifPresent(
            existing -> {
              throw new IllegalArgumentException(
                  "Ce nom d'utilisateur est déjà utilisé.");
            });
  }
}
