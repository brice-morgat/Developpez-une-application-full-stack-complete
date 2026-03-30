package com.openclassrooms.mddapi.config;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.util.Map;
import org.junit.jupiter.api.Test;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;

class JwtServiceTest {

  private static final String SECRET = "not_base64_secret_value_with_min_32_bytes_123456";

  @Test
  void generateAndValidateToken_shouldWork() {
    JwtService jwtService = new JwtService(SECRET, 60_000);
    UserDetails user = User.withUsername("alice").password("x").authorities("ROLE_USER").build();

    String token = jwtService.generateToken(user, Map.of("userId", 1L));

    assertThat(jwtService.extractUsername(token)).isEqualTo("alice");
    assertThat(jwtService.isTokenValid(token, user)).isTrue();
  }

  @Test
  void isTokenValid_shouldFailForDifferentUser() {
    JwtService jwtService = new JwtService(SECRET, 60_000);
    UserDetails alice = User.withUsername("alice").password("x").authorities("ROLE_USER").build();
    UserDetails bob = User.withUsername("bob").password("x").authorities("ROLE_USER").build();

    String token = jwtService.generateToken(alice, Map.of());

    assertThat(jwtService.isTokenValid(token, bob)).isFalse();
  }

  @Test
  void constructor_shouldFailWhenSecretTooShort() {
    assertThatThrownBy(() -> new JwtService("short-secret", 1_000))
        .isInstanceOf(IllegalStateException.class)
        .hasMessageContaining("at least 32 bytes");
  }
}
