package com.openclassrooms.mddapi.mapper;

import com.openclassrooms.mddapi.dto.PostDto;
import com.openclassrooms.mddapi.model.Post;
import org.springframework.stereotype.Component;

@Component
public class PostMapper {

  public PostDto toDto(Post post) {
    return new PostDto(
        post.getId(),
        post.getTitle(),
        post.getContent(),
        post.getAuthor().getId(),
        post.getTopic().getId(),
        post.getCreatedAt());
  }
}

