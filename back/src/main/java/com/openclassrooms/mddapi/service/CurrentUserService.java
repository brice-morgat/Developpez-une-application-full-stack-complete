package com.openclassrooms.mddapi.service;

import com.openclassrooms.mddapi.exception.ResourceNotFoundException;
import com.openclassrooms.mddapi.model.User;
import com.openclassrooms.mddapi.repository.UserRepository;
import org.springframework.security.authentication.AuthenticationCredentialsNotFoundException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

@Service
public class CurrentUserService {

  private final UserRepository userRepository;

  public CurrentUserService(UserRepository userRepository) {
    this.userRepository = userRepository;
  }

  public User getCurrentUser() {
    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
    if (authentication == null || authentication.getName() == null) {
      throw new AuthenticationCredentialsNotFoundException("Utilisateur non authentifié.");
    }
    String username = authentication.getName();
    return userRepository
        .findByUsername(username)
        .orElseThrow(() -> new AuthenticationCredentialsNotFoundException("Utilisateur introuvable."));
  }
}

