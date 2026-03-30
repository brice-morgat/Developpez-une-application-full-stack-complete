package com.openclassrooms.mddapi.mapper;

import static org.assertj.core.api.Assertions.assertThat;

import com.openclassrooms.mddapi.model.User;
import java.util.List;
import org.junit.jupiter.api.Test;

class UserMapperTest {

  private final UserMapper userMapper = new UserMapper();

  @Test
  void toAuthUser_shouldMapAllFields() {
    User user = User.builder().id(1L).email("mail@test.com").username("alice").build();

    var result = userMapper.toAuthUser(user);

    assertThat(result.id()).isEqualTo(1L);
    assertThat(result.email()).isEqualTo("mail@test.com");
    assertThat(result.username()).isEqualTo("alice");
  }

  @Test
  void toMe_shouldMapAllFields() {
    User user = User.builder().id(1L).email("mail@test.com").username("alice").build();

    var result = userMapper.toMe(user, List.of(2L, 3L));

    assertThat(result.subscriptions()).containsExactly(2L, 3L);
  }
}
