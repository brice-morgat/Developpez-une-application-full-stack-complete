package com.openclassrooms.mddapi.service;

import com.openclassrooms.mddapi.dto.CommentDto;
import com.openclassrooms.mddapi.dto.CreateCommentRequestDto;
import com.openclassrooms.mddapi.dto.CreatePostRequestDto;
import com.openclassrooms.mddapi.dto.FeedPostDto;
import com.openclassrooms.mddapi.dto.PostAuthorDto;
import com.openclassrooms.mddapi.dto.PostDetailDto;
import com.openclassrooms.mddapi.dto.PostTopicDto;
import com.openclassrooms.mddapi.exception.ResourceNotFoundException;
import com.openclassrooms.mddapi.model.Comment;
import com.openclassrooms.mddapi.model.Post;
import com.openclassrooms.mddapi.model.Topic;
import com.openclassrooms.mddapi.model.User;
import com.openclassrooms.mddapi.repository.CommentRepository;
import com.openclassrooms.mddapi.repository.PostRepository;
import com.openclassrooms.mddapi.repository.SubscriptionRepository;
import com.openclassrooms.mddapi.repository.TopicRepository;
import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import org.springframework.stereotype.Service;

@Service
public class PostService {

  private final PostRepository postRepository;
  private final TopicRepository topicRepository;
  private final SubscriptionRepository subscriptionRepository;
  private final CommentRepository commentRepository;
  private final CurrentUserService currentUserService;

  public PostService(
      PostRepository postRepository,
      TopicRepository topicRepository,
      SubscriptionRepository subscriptionRepository,
      CommentRepository commentRepository,
      CurrentUserService currentUserService) {
    this.postRepository = postRepository;
    this.topicRepository = topicRepository;
    this.subscriptionRepository = subscriptionRepository;
    this.commentRepository = commentRepository;
    this.currentUserService = currentUserService;
  }

  public List<FeedPostDto> getFeed(String sort) {
    User user = currentUserService.getCurrentUser();
    List<Long> topicIds =
        subscriptionRepository.findAllByUserId(user.getId()).stream()
            .map(subscription -> subscription.getTopic().getId())
            .toList();

    if (topicIds.isEmpty()) {
      return List.of();
    }

    Comparator<Post> comparator = Comparator.comparing(Post::getCreatedAt);
    if (!"asc".equalsIgnoreCase(sort)) {
      comparator = comparator.reversed();
    }

    return postRepository.findAllByTopicIdIn(topicIds).stream().sorted(comparator).map(this::toFeedDto).toList();
  }

  public FeedPostDto createPost(CreatePostRequestDto request) {
    User author = currentUserService.getCurrentUser();
    Topic topic =
        topicRepository
            .findById(request.topicId())
            .orElseThrow(() -> new ResourceNotFoundException("Topic not found for id " + request.topicId()));

    Post post =
        postRepository.save(
            Post.builder()
                .title(request.title().trim())
                .content(request.content().trim())
                .author(author)
                .topic(topic)
                .createdAt(LocalDateTime.now())
                .build());

    return toFeedDto(post);
  }

  public PostDetailDto getPostWithComments(Long postId) {
    Post post =
        postRepository
            .findById(postId)
            .orElseThrow(() -> new ResourceNotFoundException("Post not found for id " + postId));

    List<CommentDto> comments =
        commentRepository.findAllByPostIdOrderByCreatedAtAsc(postId).stream().map(this::toCommentDto).toList();

    return new PostDetailDto(
        post.getId(),
        post.getTitle(),
        post.getContent(),
        toAuthorDto(post.getAuthor()),
        toTopicDto(post.getTopic()),
        post.getCreatedAt(),
        comments);
  }

  public CommentDto addComment(Long postId, CreateCommentRequestDto request) {
    User author = currentUserService.getCurrentUser();
    Post post =
        postRepository
            .findById(postId)
            .orElseThrow(() -> new ResourceNotFoundException("Post not found for id " + postId));

    Comment comment =
        commentRepository.save(
            Comment.builder()
                .post(post)
                .author(author)
                .content(request.content().trim())
                .createdAt(LocalDateTime.now())
                .build());

    return toCommentDto(comment);
  }

  private FeedPostDto toFeedDto(Post post) {
    return new FeedPostDto(
        post.getId(),
        post.getTitle(),
        post.getContent(),
        toAuthorDto(post.getAuthor()),
        toTopicDto(post.getTopic()),
        post.getCreatedAt());
  }

  private CommentDto toCommentDto(Comment comment) {
    return new CommentDto(
        comment.getId(), comment.getContent(), toAuthorDto(comment.getAuthor()), comment.getCreatedAt());
  }

  private PostAuthorDto toAuthorDto(User user) {
    return new PostAuthorDto(user.getId(), user.getUsername());
  }

  private PostTopicDto toTopicDto(Topic topic) {
    return new PostTopicDto(topic.getId(), topic.getName());
  }
}
