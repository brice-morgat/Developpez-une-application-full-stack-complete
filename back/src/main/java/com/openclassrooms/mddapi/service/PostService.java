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
  private final CurrentAuthenticatedUserProvider currentAuthenticatedUserProvider;

  public PostService(
      PostRepository postRepository,
      TopicRepository topicRepository,
      SubscriptionRepository subscriptionRepository,
      CommentRepository commentRepository,
      CurrentAuthenticatedUserProvider currentAuthenticatedUserProvider) {
    this.postRepository = postRepository;
    this.topicRepository = topicRepository;
    this.subscriptionRepository = subscriptionRepository;
    this.commentRepository = commentRepository;
    this.currentAuthenticatedUserProvider = currentAuthenticatedUserProvider;
  }

  public List<Post> getFeed(String sort) {
    User user = currentAuthenticatedUserProvider.getCurrentUser();
    List<Long> topicIds = subscriptionRepository.findTopicIdsByUserId(user.getId());

    if (topicIds.isEmpty()) {
      return List.of();
    }

    if ("asc".equalsIgnoreCase(sort)) {
      return postRepository.findAllByTopicIdInOrderByCreatedAtAsc(topicIds);
    }

    return postRepository.findAllByTopicIdInOrderByCreatedAtDesc(topicIds);
  }

  @Transactional
  public Post createPost(CreatePostRequestDto request) {
    User author = currentAuthenticatedUserProvider.getCurrentUser();
    Topic topic =
        topicRepository
            .findById(request.topicId())
            .orElseThrow(() -> new ResourceNotFoundException("Thème introuvable."));

    if (!subscriptionRepository.existsByUserIdAndTopicId(author.getId(), topic.getId())) {
      throw new ForbiddenOperationException(
          "Vous devez être abonné au thème pour publier un article.");
    }

    return postRepository.save(
        Post.builder()
            .title(request.title().trim())
            .content(request.content().trim())
            .author(author)
            .topic(topic)
            .createdAt(LocalDateTime.now())
            .build());
  }

  public PostWithComments getPostWithComments(Long postId) {
    Post post = findPostById(postId);
    List<Comment> comments = commentRepository.findAllByPostIdOrderByCreatedAtAsc(postId);
    return new PostWithComments(post, comments);
  }

  @Transactional
  public Comment addComment(Long postId, CreateCommentRequestDto request) {
    User author = currentAuthenticatedUserProvider.getCurrentUser();
    Post post = findPostById(postId);

    return commentRepository.save(
        Comment.builder()
            .post(post)
            .author(author)
            .content(request.content().trim())
            .createdAt(LocalDateTime.now())
            .build());
  }

  private Post findPostById(Long postId) {
    return postRepository
        .findDetailedById(postId)
        .orElseThrow(() -> new ResourceNotFoundException("Article introuvable."));
  }
}
