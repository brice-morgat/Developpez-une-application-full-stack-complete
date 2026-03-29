package com.openclassrooms.mddapi.controller;

import com.openclassrooms.mddapi.dto.CommentDto;
import com.openclassrooms.mddapi.dto.CreateCommentRequestDto;
import com.openclassrooms.mddapi.dto.CreatePostRequestDto;
import com.openclassrooms.mddapi.dto.FeedPostDto;
import com.openclassrooms.mddapi.dto.PostDetailDto;
import com.openclassrooms.mddapi.mapper.PostMapper;
import com.openclassrooms.mddapi.service.PostService;
import jakarta.validation.Valid;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
public class PostController {

  private final PostService postService;
  private final PostMapper postMapper;

  public PostController(PostService postService, PostMapper postMapper) {
    this.postService = postService;
    this.postMapper = postMapper;
  }

  @GetMapping("/feed")
  public List<FeedPostDto> getFeed(@RequestParam(defaultValue = "desc") String sort) {
    return postMapper.toFeedPostDtos(postService.getFeed(sort));
  }

  @PostMapping("/posts")
  @ResponseStatus(HttpStatus.CREATED)
  public FeedPostDto createPost(@Valid @RequestBody CreatePostRequestDto request) {
    return postMapper.toFeedPostDto(postService.createPost(request));
  }

  @GetMapping("/posts/{postId}")
  public PostDetailDto getPost(@PathVariable Long postId) {
    return postMapper.toPostDetailDto(postService.getPostWithComments(postId));
  }

  @PostMapping("/posts/{postId}/comments")
  @ResponseStatus(HttpStatus.CREATED)
  public CommentDto createComment(
      @PathVariable Long postId, @Valid @RequestBody CreateCommentRequestDto request) {
    return postMapper.toCommentDto(postService.addComment(postId, request));
  }
}
