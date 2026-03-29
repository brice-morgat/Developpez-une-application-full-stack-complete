package com.openclassrooms.mddapi.mapper;

import com.openclassrooms.mddapi.dto.CommentDto;
import com.openclassrooms.mddapi.dto.FeedPostDto;
import com.openclassrooms.mddapi.dto.PostAuthorDto;
import com.openclassrooms.mddapi.dto.PostDetailDto;
import com.openclassrooms.mddapi.dto.PostTopicDto;
import com.openclassrooms.mddapi.model.Comment;
import com.openclassrooms.mddapi.model.Post;
import com.openclassrooms.mddapi.model.Topic;
import com.openclassrooms.mddapi.model.User;
import com.openclassrooms.mddapi.service.PostWithComments;
import java.util.List;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface PostMapper {

  FeedPostDto toFeedPostDto(Post post);

  List<FeedPostDto> toFeedPostDtos(List<Post> posts);

  CommentDto toCommentDto(Comment comment);

  List<CommentDto> toCommentDtos(List<Comment> comments);

  PostAuthorDto toPostAuthorDto(User user);

  PostTopicDto toPostTopicDto(Topic topic);

  @Mapping(target = "id", source = "post.id")
  @Mapping(target = "title", source = "post.title")
  @Mapping(target = "content", source = "post.content")
  @Mapping(target = "author", source = "post.author")
  @Mapping(target = "topic", source = "post.topic")
  @Mapping(target = "createdAt", source = "post.createdAt")
  @Mapping(target = "comments", source = "comments")
  PostDetailDto toPostDetailDto(PostWithComments postWithComments);
}
