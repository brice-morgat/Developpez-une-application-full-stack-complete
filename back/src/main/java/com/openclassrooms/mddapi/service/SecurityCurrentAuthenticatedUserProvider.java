package com.openclassrooms.mddapi.service;

import com.openclassrooms.mddapi.model.User;
import com.openclassrooms.mddapi.repository.UserRepository;
import org.springframework.security.authentication.AuthenticationCredentialsNotFoundException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

@Service
public class SecurityCurrentAuthenticatedUserProvider implements CurrentAuthenticatedUserProvider {

  private final UserRepository userRepository;

  public SecurityCurrentAuthenticatedUserProvider(UserRepository userRepository) {
    this.userRepository = userRepository;
  }

  @Override
  public User getCurrentUser() {
    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
    if (authentication == null || authentication.getName() == null) {
      throw new AuthenticationCredentialsNotFoundException(
          "Utilisateur non authentifié.");
    }

    return userRepository
        .findByUsername(authentication.getName())
        .orElseThrow(
            () -> new AuthenticationCredentialsNotFoundException("Utilisateur introuvable."));
  }
}
