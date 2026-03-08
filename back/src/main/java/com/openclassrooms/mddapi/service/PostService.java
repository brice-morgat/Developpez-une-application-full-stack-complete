package com.openclassrooms.mddapi.service;

import com.openclassrooms.mddapi.dto.PostDto;
import com.openclassrooms.mddapi.repository.PostRepository;
import java.util.List;
import org.springframework.stereotype.Service;

@Service
public class PostService {

  private final PostRepository postRepository;

  public PostService(PostRepository postRepository) {
    this.postRepository = postRepository;
  }

  public List<PostDto> getFeed() {
    return postRepository.findAll().stream()
        .map(
            post ->
                new PostDto(
                    post.getId(),
                    post.getTitle(),
                    post.getContent(),
                    post.getAuthor().getId(),
                    post.getTopic().getId(),
                    post.getCreatedAt()))
        .toList();
  }
}

