package com.openclassrooms.mddapi.controller;

import com.openclassrooms.mddapi.dto.CommentDto;
import com.openclassrooms.mddapi.dto.CreateCommentRequestDto;
import com.openclassrooms.mddapi.dto.CreatePostRequestDto;
import com.openclassrooms.mddapi.dto.FeedPostDto;
import com.openclassrooms.mddapi.dto.PostDetailDto;
import com.openclassrooms.mddapi.service.PostService;
import jakarta.validation.Valid;
import java.util.List;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
public class PostController {

  private final PostService postService;

  public PostController(PostService postService) {
    this.postService = postService;
  }

  @GetMapping("/feed")
  public List<FeedPostDto> getFeed(@RequestParam(defaultValue = "desc") String sort) {
    return postService.getFeed(sort);
  }

  @PostMapping("/posts")
  public FeedPostDto createPost(@Valid @RequestBody CreatePostRequestDto request) {
    return postService.createPost(request);
  }

  @GetMapping("/posts/{postId}")
  public PostDetailDto getPost(@PathVariable Long postId) {
    return postService.getPostWithComments(postId);
  }

  @PostMapping("/posts/{postId}/comments")
  public CommentDto createComment(
      @PathVariable Long postId, @Valid @RequestBody CreateCommentRequestDto request) {
    return postService.addComment(postId, request);
  }
}

