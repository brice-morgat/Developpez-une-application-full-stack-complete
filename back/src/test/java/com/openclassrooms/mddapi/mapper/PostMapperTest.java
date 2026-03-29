package com.openclassrooms.mddapi.mapper;

import static org.assertj.core.api.Assertions.assertThat;

import com.openclassrooms.mddapi.model.Comment;
import com.openclassrooms.mddapi.model.Post;
import com.openclassrooms.mddapi.model.Topic;
import com.openclassrooms.mddapi.model.User;
import com.openclassrooms.mddapi.service.PostWithComments;
import java.time.LocalDateTime;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

class PostMapperTest {

  private final PostMapper postMapper = Mappers.getMapper(PostMapper.class);

  @Test
  void toFeedPostDto_shouldMapAllFields() {
    User user = User.builder().id(1L).username("alice").build();
    Topic topic = Topic.builder().id(2L).name("Java").description("Java desc").build();
    LocalDateTime createdAt = LocalDateTime.now();
    Post post =
        Post.builder().id(3L).title("title").content("content").author(user).topic(topic).createdAt(createdAt).build();

    var result = postMapper.toFeedPostDto(post);

    assertThat(result.id()).isEqualTo(3L);
    assertThat(result.author().id()).isEqualTo(1L);
    assertThat(result.author().username()).isEqualTo("alice");
    assertThat(result.topic().id()).isEqualTo(2L);
    assertThat(result.topic().description()).isEqualTo("Java desc");
    assertThat(result.createdAt()).isEqualTo(createdAt);
  }

  @Test
  void toPostDetailDto_shouldMapPostAndComments() {
    User user = User.builder().id(1L).username("alice").build();
    Topic topic = Topic.builder().id(2L).name("Java").description("Java desc").build();
    LocalDateTime createdAt = LocalDateTime.now();
    Post post =
        Post.builder().id(3L).title("title").content("content").author(user).topic(topic).createdAt(createdAt).build();
    Comment comment =
        Comment.builder().id(4L).content("hello").author(user).post(post).createdAt(createdAt.plusMinutes(1)).build();

    var result = postMapper.toPostDetailDto(new PostWithComments(post, List.of(comment)));

    assertThat(result.id()).isEqualTo(3L);
    assertThat(result.topic().description()).isEqualTo("Java desc");
    assertThat(result.comments()).hasSize(1);
    assertThat(result.comments().get(0).content()).isEqualTo("hello");
  }
}
