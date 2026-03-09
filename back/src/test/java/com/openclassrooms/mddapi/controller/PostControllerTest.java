package com.openclassrooms.mddapi.controller;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.openclassrooms.mddapi.dto.CommentDto;
import com.openclassrooms.mddapi.dto.CreateCommentRequestDto;
import com.openclassrooms.mddapi.dto.CreatePostRequestDto;
import com.openclassrooms.mddapi.dto.FeedPostDto;
import com.openclassrooms.mddapi.dto.PostAuthorDto;
import com.openclassrooms.mddapi.dto.PostDetailDto;
import com.openclassrooms.mddapi.dto.PostTopicDto;
import com.openclassrooms.mddapi.service.PostService;
import java.time.LocalDateTime;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class PostControllerTest {

  @Mock private PostService postService;

  @InjectMocks private PostController postController;

  @Test
  void getFeed_shouldReturnServiceValue() {
    FeedPostDto post = new FeedPostDto(1L, "Title", "Content", new PostAuthorDto(1L, "alice"), new PostTopicDto(2L, "Java"), LocalDateTime.now());
    when(postService.getFeed("asc")).thenReturn(List.of(post));

    List<FeedPostDto> result = postController.getFeed("asc");

    verify(postService).getFeed("asc");
    assertThat(result).hasSize(1);
  }

  @Test
  void createPost_shouldDelegateToService() {
    CreatePostRequestDto request = new CreatePostRequestDto(2L, "Title", "Content");
    FeedPostDto response = new FeedPostDto(1L, "Title", "Content", new PostAuthorDto(1L, "alice"), new PostTopicDto(2L, "Java"), LocalDateTime.now());
    when(postService.createPost(request)).thenReturn(response);

    FeedPostDto result = postController.createPost(request);

    verify(postService).createPost(request);
    assertThat(result).isEqualTo(response);
  }

  @Test
  void getPost_shouldDelegateToService() {
    PostDetailDto response = new PostDetailDto(1L, "Title", "Body", new PostAuthorDto(1L, "alice"), new PostTopicDto(2L, "Java"), LocalDateTime.now(), List.of());
    when(postService.getPostWithComments(1L)).thenReturn(response);

    PostDetailDto result = postController.getPost(1L);

    verify(postService).getPostWithComments(1L);
    assertThat(result).isEqualTo(response);
  }

  @Test
  void createComment_shouldDelegateToService() {
    CreateCommentRequestDto request = new CreateCommentRequestDto("hello");
    CommentDto response = new CommentDto(1L, "hello", new PostAuthorDto(1L, "alice"), LocalDateTime.now());
    when(postService.addComment(1L, request)).thenReturn(response);

    CommentDto result = postController.createComment(1L, request);

    verify(postService).addComment(1L, request);
    assertThat(result).isEqualTo(response);
  }
}
