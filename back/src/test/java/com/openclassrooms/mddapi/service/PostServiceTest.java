package com.openclassrooms.mddapi.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import com.openclassrooms.mddapi.dto.CreateCommentRequestDto;
import com.openclassrooms.mddapi.dto.CreatePostRequestDto;
import com.openclassrooms.mddapi.exception.ResourceNotFoundException;
import com.openclassrooms.mddapi.model.Comment;
import com.openclassrooms.mddapi.model.Post;
import com.openclassrooms.mddapi.model.Subscription;
import com.openclassrooms.mddapi.model.Topic;
import com.openclassrooms.mddapi.model.User;
import com.openclassrooms.mddapi.repository.CommentRepository;
import com.openclassrooms.mddapi.repository.PostRepository;
import com.openclassrooms.mddapi.repository.SubscriptionRepository;
import com.openclassrooms.mddapi.repository.TopicRepository;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class PostServiceTest {

  @Mock private PostRepository postRepository;
  @Mock private TopicRepository topicRepository;
  @Mock private SubscriptionRepository subscriptionRepository;
  @Mock private CommentRepository commentRepository;
  @Mock private CurrentUserService currentUserService;

  @InjectMocks private PostService postService;

  @Test
  void getFeed_shouldReturnEmptyWithoutSubscriptions() {
    User user = User.builder().id(1L).build();
    when(currentUserService.getCurrentUser()).thenReturn(user);
    when(subscriptionRepository.findAllByUserId(1L)).thenReturn(List.of());

    assertThat(postService.getFeed("desc")).isEmpty();
  }

  @Test
  void getFeed_shouldSortDescendingByDefault() {
    User user = User.builder().id(1L).build();
    Topic topic = Topic.builder().id(2L).name("Java").build();
    User author = User.builder().id(9L).username("bob").build();

    Post older = Post.builder().id(1L).title("A").content("a").topic(topic).author(author).createdAt(LocalDateTime.now().minusDays(1)).build();
    Post newer = Post.builder().id(2L).title("B").content("b").topic(topic).author(author).createdAt(LocalDateTime.now()).build();

    when(currentUserService.getCurrentUser()).thenReturn(user);
    when(subscriptionRepository.findAllByUserId(1L)).thenReturn(List.of(Subscription.builder().topic(topic).build()));
    when(postRepository.findAllByTopicIdIn(List.of(2L))).thenReturn(List.of(older, newer));

    var result = postService.getFeed("desc");

    assertThat(result).hasSize(2);
    assertThat(result.get(0).id()).isEqualTo(2L);
  }

  @Test
  void createPost_shouldTrimFields() {
    User user = User.builder().id(1L).username("alice").build();
    Topic topic = Topic.builder().id(3L).name("Angular").build();

    when(currentUserService.getCurrentUser()).thenReturn(user);
    when(topicRepository.findById(3L)).thenReturn(Optional.of(topic));
    when(postRepository.save(any(Post.class)))
        .thenAnswer(invocation -> {
          Post p = invocation.getArgument(0);
          p.setId(44L);
          return p;
        });

    var result = postService.createPost(new CreatePostRequestDto(3L, "  Title  ", "  Content  "));

    assertThat(result.id()).isEqualTo(44L);
    assertThat(result.title()).isEqualTo("Title");
    assertThat(result.content()).isEqualTo("Content");
  }

  @Test
  void getPostWithComments_shouldReturnDetail() {
    User author = User.builder().id(1L).username("alice").build();
    Topic topic = Topic.builder().id(2L).name("Java").build();
    Post post = Post.builder().id(7L).title("T").content("C").author(author).topic(topic).createdAt(LocalDateTime.now()).build();
    Comment comment = Comment.builder().id(9L).content("nice").author(author).post(post).createdAt(LocalDateTime.now()).build();

    when(postRepository.findById(7L)).thenReturn(Optional.of(post));
    when(commentRepository.findAllByPostIdOrderByCreatedAtAsc(7L)).thenReturn(List.of(comment));

    var result = postService.getPostWithComments(7L);

    assertThat(result.comments()).hasSize(1);
    assertThat(result.comments().get(0).content()).isEqualTo("nice");
  }

  @Test
  void addComment_shouldFailWhenPostMissing() {
    when(currentUserService.getCurrentUser()).thenReturn(User.builder().id(1L).username("alice").build());
    when(postRepository.findById(999L)).thenReturn(Optional.empty());

    assertThatThrownBy(() -> postService.addComment(999L, new CreateCommentRequestDto("test")))
        .isInstanceOf(ResourceNotFoundException.class)
        .hasMessageContaining("Post not found");
  }
}
