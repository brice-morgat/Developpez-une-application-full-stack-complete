package com.openclassrooms.mddapi.mapper;

import static org.assertj.core.api.Assertions.assertThat;

import com.openclassrooms.mddapi.model.Post;
import com.openclassrooms.mddapi.model.Topic;
import com.openclassrooms.mddapi.model.User;
import java.time.LocalDateTime;
import org.junit.jupiter.api.Test;

class PostMapperTest {

  private final PostMapper postMapper = new PostMapper();

  @Test
  void toDto_shouldMapAllFields() {
    User user = User.builder().id(1L).build();
    Topic topic = Topic.builder().id(2L).build();
    LocalDateTime createdAt = LocalDateTime.now();
    Post post = Post.builder().id(3L).title("title").content("content").author(user).topic(topic).createdAt(createdAt).build();

    var result = postMapper.toDto(post);

    assertThat(result.id()).isEqualTo(3L);
    assertThat(result.authorId()).isEqualTo(1L);
    assertThat(result.topicId()).isEqualTo(2L);
    assertThat(result.createdAt()).isEqualTo(createdAt);
  }
}
