package com.openclassrooms.mddapi.service;

import com.openclassrooms.mddapi.dto.CreateCommentRequestDto;
import com.openclassrooms.mddapi.dto.CreatePostRequestDto;
import com.openclassrooms.mddapi.exception.ForbiddenOperationException;
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
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
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

  public List<Post> getFeed(String sort) {
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

    return postRepository.findAllByTopicIdIn(topicIds).stream()
        .sorted(comparator)
        .peek(this::initializePostRelations)
        .toList();
  }

  @Transactional
  public Post createPost(CreatePostRequestDto request) {
    User author = currentUserService.getCurrentUser();
    Topic topic =
        topicRepository
            .findById(request.topicId())
            .orElseThrow(() -> new ResourceNotFoundException("Thème introuvable."));

    if (!subscriptionRepository.existsByUserIdAndTopicId(author.getId(), topic.getId())) {
      throw new ForbiddenOperationException("Vous devez être abonné au thème pour publier un article.");
    }

    Post post =
        postRepository.save(
            Post.builder()
                .title(request.title().trim())
                .content(request.content().trim())
                .author(author)
                .topic(topic)
                .createdAt(LocalDateTime.now())
                .build());
    initializePostRelations(post);
    return post;
  }

  public PostWithComments getPostWithComments(Long postId) {
    Post post = findPostById(postId);
    List<Comment> comments = findCommentsByPostId(postId);
    initializePostRelations(post);
    comments.forEach(this::initializeCommentRelations);
    return new PostWithComments(post, comments);
  }

  @Transactional
  public Comment addComment(Long postId, CreateCommentRequestDto request) {
    User author = currentUserService.getCurrentUser();
    Post post = findPostById(postId);

    Comment comment =
        commentRepository.save(
            Comment.builder()
                .post(post)
                .author(author)
                .content(request.content().trim())
                .createdAt(LocalDateTime.now())
                .build());
    initializeCommentRelations(comment);
    return comment;
  }

  private Post findPostById(Long postId) {
    return postRepository
        .findById(postId)
        .orElseThrow(() -> new ResourceNotFoundException("Article introuvable."));
  }

  private List<Comment> findCommentsByPostId(Long postId) {
    return commentRepository.findAllByPostIdOrderByCreatedAtAsc(postId);
  }

  private void initializePostRelations(Post post) {
    post.getAuthor().getUsername();
    post.getTopic().getName();
    post.getTopic().getDescription();
  }

  private void initializeCommentRelations(Comment comment) {
    comment.getAuthor().getUsername();
    comment.getPost().getId();
  }
}
