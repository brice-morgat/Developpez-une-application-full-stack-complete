package com.openclassrooms.mddapi.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;

import com.openclassrooms.mddapi.dto.UpdateMeRequestDto;
import com.openclassrooms.mddapi.mapper.UserMapper;
import com.openclassrooms.mddapi.model.User;
import com.openclassrooms.mddapi.repository.SubscriptionRepository;
import com.openclassrooms.mddapi.repository.UserRepository;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

  @Mock private CurrentAuthenticatedUserProvider currentAuthenticatedUserProvider;
  @Mock private SubscriptionRepository subscriptionRepository;
  @Mock private UserRepository userRepository;
  @Mock private PasswordEncoder passwordEncoder;
  @Mock private UserMapper userMapper;
  @Mock private UserIdentityNormalizer userIdentityNormalizer;
  @Mock private UserUniquenessValidator userUniquenessValidator;

  @InjectMocks private UserService userService;

  @Test
  void getCurrentUserProfile_shouldReturnMappedProfile() {
    User current = User.builder().id(1L).username("alice").email("alice@mail.com").build();

    when(currentAuthenticatedUserProvider.getCurrentUser()).thenReturn(current);
    when(subscriptionRepository.findTopicIdsByUserId(1L)).thenReturn(List.of(3L));
    when(userMapper.toMe(current, List.of(3L)))
        .thenReturn(
            new com.openclassrooms.mddapi.dto.MeResponseDto(
                1L, "alice@mail.com", "alice", List.of(3L)));

    var result = userService.getCurrentUserProfile();

    assertThat(result.username()).isEqualTo("alice");
    assertThat(result.subscriptions()).containsExactly(3L);
  }

  @Test
  void updateCurrentUser_shouldFailWhenEmailAlreadyUsed() {
    User current = User.builder().id(1L).username("alice").email("alice@mail.com").build();

    when(currentAuthenticatedUserProvider.getCurrentUser()).thenReturn(current);
    when(userIdentityNormalizer.normalizeEmail("taken@mail.com")).thenReturn("taken@mail.com");
    org.mockito.Mockito.doThrow(
            new IllegalArgumentException("Cet e-mail est déjà utilisé."))
        .when(userUniquenessValidator)
        .ensureEmailAvailableForUpdate("taken@mail.com", 1L);

    assertThatThrownBy(
            () -> userService.updateCurrentUser(new UpdateMeRequestDto("taken@mail.com", null, null)))
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessage("Cet e-mail est déjà utilisé.");
  }

  @Test
  void updateCurrentUser_shouldUpdatePasswordWhenProvided() {
    User current =
        User.builder().id(1L).username("alice").email("alice@mail.com").password("old").build();

    when(currentAuthenticatedUserProvider.getCurrentUser()).thenReturn(current);
    when(userIdentityNormalizer.normalizeEmail("new@mail.com")).thenReturn("new@mail.com");
    when(userIdentityNormalizer.normalizeUsername("Alice2")).thenReturn("Alice2");
    when(passwordEncoder.encode("secret123")).thenReturn("encoded");
    when(userRepository.save(current)).thenReturn(current);
    when(subscriptionRepository.findTopicIdsByUserId(1L)).thenReturn(List.of());
    when(userMapper.toMe(current, List.of()))
        .thenReturn(
            new com.openclassrooms.mddapi.dto.MeResponseDto(
                1L, "new@mail.com", "Alice2", List.of()));

    var result =
        userService.updateCurrentUser(new UpdateMeRequestDto("new@mail.com", "Alice2", "secret123"));

    assertThat(current.getPassword()).isEqualTo("encoded");
    assertThat(result.email()).isEqualTo("new@mail.com");
    assertThat(result.username()).isEqualTo("Alice2");
  }
}
