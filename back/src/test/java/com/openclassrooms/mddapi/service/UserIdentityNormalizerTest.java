package com.openclassrooms.mddapi.service;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

class UserIdentityNormalizerTest {

  private final UserIdentityNormalizer normalizer = new UserIdentityNormalizer();

  @Test
  void shouldNormalizeEmail() {
    assertThat(normalizer.normalizeEmail("  TEST@MAIL.COM  ")).isEqualTo("test@mail.com");
  }

  @Test
  void shouldNormalizeUsername() {
    assertThat(normalizer.normalizeUsername("  Alice  ")).isEqualTo("Alice");
  }

  @Test
  void shouldNormalizeIdentifier() {
    assertThat(normalizer.normalizeIdentifier("  Alice  ")).isEqualTo("Alice");
  }
}
