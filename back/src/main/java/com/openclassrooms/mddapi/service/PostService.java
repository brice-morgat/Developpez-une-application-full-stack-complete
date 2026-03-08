package com.openclassrooms.mddapi.service;

import com.openclassrooms.mddapi.dto.PostDto;
import com.openclassrooms.mddapi.exception.ResourceNotFoundException;
import com.openclassrooms.mddapi.mapper.PostMapper;
import com.openclassrooms.mddapi.repository.PostRepository;
import java.util.List;
import org.springframework.stereotype.Service;

@Service
public class PostService {

  private final PostRepository postRepository;
  private final PostMapper postMapper;

  public PostService(PostRepository postRepository, PostMapper postMapper) {
    this.postRepository = postRepository;
    this.postMapper = postMapper;
  }

  public List<PostDto> getFeed() {
    return postRepository.findAll().stream().map(postMapper::toDto).toList();
  }

  public PostDto getPostById(Long id) {
    return postRepository
        .findById(id)
        .map(postMapper::toDto)
        .orElseThrow(() -> new ResourceNotFoundException("Post not found for id " + id));
  }
}

