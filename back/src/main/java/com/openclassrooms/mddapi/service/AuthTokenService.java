package com.openclassrooms.mddapi.service;

import com.openclassrooms.mddapi.config.JwtService;
import com.openclassrooms.mddapi.model.User;
import java.util.Map;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

@Service
public class AuthTokenService {

  private final JwtService jwtService;

  public AuthTokenService(JwtService jwtService) {
    this.jwtService = jwtService;
  }

  public String generateToken(User user) {
    UserDetails userDetails =
        org.springframework.security.core.userdetails.User.withUsername(user.getUsername())
            .password(user.getPassword())
            .authorities("ROLE_USER")
            .build();

    return jwtService.generateToken(userDetails, Map.of("userId", user.getId()));
  }
}
