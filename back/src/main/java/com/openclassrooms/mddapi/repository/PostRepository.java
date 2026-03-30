package com.openclassrooms.mddapi.repository;

import com.openclassrooms.mddapi.model.Post;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PostRepository extends JpaRepository<Post, Long> {
  List<Post> findAllByTopicIdOrderByCreatedAtDesc(Long topicId);

  List<Post> findAllByAuthorIdOrderByCreatedAtDesc(Long authorId);

  @EntityGraph(attributePaths = {"author", "topic"})
  List<Post> findAllByTopicIdInOrderByCreatedAtAsc(List<Long> topicIds);

  @EntityGraph(attributePaths = {"author", "topic"})
  List<Post> findAllByTopicIdInOrderByCreatedAtDesc(List<Long> topicIds);

  @EntityGraph(attributePaths = {"author", "topic"})
  Optional<Post> findDetailedById(Long id);
}
