package com.openclassrooms.mddapi.service;

import org.springframework.stereotype.Component;

@Component
public class UserIdentityNormalizer {

  public String normalizeEmail(String email) {
    return email.trim().toLowerCase();
  }

  public String normalizeUsername(String username) {
    return username.trim();
  }

  public String normalizeIdentifier(String identifier) {
    return identifier.trim();
  }
}
