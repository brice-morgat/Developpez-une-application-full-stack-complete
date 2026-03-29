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
import com.openclassrooms.mddapi.mapper.PostMapper;
import com.openclassrooms.mddapi.model.Comment;
import com.openclassrooms.mddapi.model.Post;
import com.openclassrooms.mddapi.model.Topic;
import com.openclassrooms.mddapi.model.User;
import com.openclassrooms.mddapi.service.PostService;
import com.openclassrooms.mddapi.service.PostWithComments;
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
  @Mock private PostMapper postMapper;

  @InjectMocks private PostController postController;

  @Test
  void getFeed_shouldReturnMappedServiceValue() {
    Topic topic = Topic.builder().id(2L).name("Java").description("Java desc").build();
    User author = User.builder().id(1L).username("alice").build();
    Post post = Post.builder().id(1L).title("Title").content("Content").author(author).topic(topic).createdAt(LocalDateTime.now()).build();
    FeedPostDto dto =
        new FeedPostDto(1L, "Title", "Content", new PostAuthorDto(1L, "alice"), new PostTopicDto(2L, "Java", "Java desc"), LocalDateTime.now());
    when(postService.getFeed("asc")).thenReturn(List.of(post));
    when(postMapper.toFeedPostDtos(List.of(post))).thenReturn(List.of(dto));

    List<FeedPostDto> result = postController.getFeed("asc");

    verify(postService).getFeed("asc");
    verify(postMapper).toFeedPostDtos(List.of(post));
    assertThat(result).containsExactly(dto);
  }

  @Test
  void createPost_shouldDelegateToServiceAndMapper() {
    CreatePostRequestDto request = new CreatePostRequestDto(2L, "Title", "Content");
    Topic topic = Topic.builder().id(2L).name("Java").description("Java desc").build();
    User author = User.builder().id(1L).username("alice").build();
    Post post = Post.builder().id(1L).title("Title").content("Content").author(author).topic(topic).createdAt(LocalDateTime.now()).build();
    FeedPostDto response =
        new FeedPostDto(1L, "Title", "Content", new PostAuthorDto(1L, "alice"), new PostTopicDto(2L, "Java", "Java desc"), LocalDateTime.now());
    when(postService.createPost(request)).thenReturn(post);
    when(postMapper.toFeedPostDto(post)).thenReturn(response);

    FeedPostDto result = postController.createPost(request);

    verify(postService).createPost(request);
    verify(postMapper).toFeedPostDto(post);
    assertThat(result).isEqualTo(response);
  }

  @Test
  void getPost_shouldDelegateToServiceAndMapper() {
    Topic topic = Topic.builder().id(2L).name("Java").description("Java desc").build();
    User author = User.builder().id(1L).username("alice").build();
    Post post = Post.builder().id(1L).title("Title").content("Body").author(author).topic(topic).createdAt(LocalDateTime.now()).build();
    PostWithComments postWithComments = new PostWithComments(post, List.of());
    PostDetailDto response =
        new PostDetailDto(1L, "Title", "Body", new PostAuthorDto(1L, "alice"), new PostTopicDto(2L, "Java", "Java desc"), LocalDateTime.now(), List.of());
    when(postService.getPostWithComments(1L)).thenReturn(postWithComments);
    when(postMapper.toPostDetailDto(postWithComments)).thenReturn(response);

    PostDetailDto result = postController.getPost(1L);

    verify(postService).getPostWithComments(1L);
    verify(postMapper).toPostDetailDto(postWithComments);
    assertThat(result).isEqualTo(response);
  }

  @Test
  void createComment_shouldDelegateToServiceAndMapper() {
    CreateCommentRequestDto request = new CreateCommentRequestDto("hello");
    Topic topic = Topic.builder().id(2L).name("Java").description("Java desc").build();
    User author = User.builder().id(1L).username("alice").build();
    Post post = Post.builder().id(1L).title("Title").content("Body").author(author).topic(topic).createdAt(LocalDateTime.now()).build();
    Comment comment = Comment.builder().id(1L).content("hello").author(author).post(post).createdAt(LocalDateTime.now()).build();
    CommentDto response = new CommentDto(1L, "hello", new PostAuthorDto(1L, "alice"), LocalDateTime.now());
    when(postService.addComment(1L, request)).thenReturn(comment);
    when(postMapper.toCommentDto(comment)).thenReturn(response);

    CommentDto result = postController.createComment(1L, request);

    verify(postService).addComment(1L, request);
    verify(postMapper).toCommentDto(comment);
    assertThat(result).isEqualTo(response);
  }
}
