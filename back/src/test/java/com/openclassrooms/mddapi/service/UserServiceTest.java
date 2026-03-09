package com.openclassrooms.mddapi.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;

import com.openclassrooms.mddapi.dto.UpdateMeRequestDto;
import com.openclassrooms.mddapi.mapper.UserMapper;
import com.openclassrooms.mddapi.model.Subscription;
import com.openclassrooms.mddapi.model.Topic;
import com.openclassrooms.mddapi.model.User;
import com.openclassrooms.mddapi.repository.SubscriptionRepository;
import com.openclassrooms.mddapi.repository.UserRepository;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

  @Mock private CurrentUserService currentUserService;
  @Mock private SubscriptionRepository subscriptionRepository;
  @Mock private UserRepository userRepository;
  @Mock private PasswordEncoder passwordEncoder;

  private final UserMapper userMapper = new UserMapper();

  @InjectMocks private UserService userService;

  @Test
  void getCurrentUserProfile_shouldReturnMappedProfile() {
    User current = User.builder().id(1L).username("alice").email("alice@mail.com").build();
    Topic topic = Topic.builder().id(3L).build();

    when(currentUserService.getCurrentUser()).thenReturn(current);
    when(subscriptionRepository.findAllByUserId(1L)).thenReturn(List.of(Subscription.builder().topic(topic).build()));

    var result = userService.getCurrentUserProfile();

    assertThat(result.username()).isEqualTo("alice");
    assertThat(result.subscriptions()).containsExactly(3L);
  }

  @Test
  void updateCurrentUser_shouldFailWhenEmailAlreadyUsed() {
    User current = User.builder().id(1L).username("alice").email("alice@mail.com").build();
    User other = User.builder().id(2L).username("bob").email("taken@mail.com").build();

    when(currentUserService.getCurrentUser()).thenReturn(current);
    when(userRepository.findByEmail("taken@mail.com")).thenReturn(Optional.of(other));

    assertThatThrownBy(() -> userService.updateCurrentUser(new UpdateMeRequestDto("taken@mail.com", null, null)))
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessage("Email is already used");
  }

  @Test
  void updateCurrentUser_shouldUpdatePasswordWhenProvided() {
    User current = User.builder().id(1L).username("alice").email("alice@mail.com").password("old").build();

    when(currentUserService.getCurrentUser()).thenReturn(current);
    when(userRepository.findByEmail("new@mail.com")).thenReturn(Optional.empty());
    when(userRepository.findByUsername("Alice2")).thenReturn(Optional.empty());
    when(passwordEncoder.encode("secret123")).thenReturn("encoded");
    when(userRepository.save(current)).thenReturn(current);
    when(subscriptionRepository.findAllByUserId(1L)).thenReturn(List.of());

    var result = userService.updateCurrentUser(new UpdateMeRequestDto("new@mail.com", "Alice2", "secret123"));

    assertThat(current.getPassword()).isEqualTo("encoded");
    assertThat(result.email()).isEqualTo("new@mail.com");
    assertThat(result.username()).isEqualTo("Alice2");
  }
}
