package com.openclassrooms.mddapi.repository;

import com.openclassrooms.mddapi.model.Subscription;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface SubscriptionRepository extends JpaRepository<Subscription, Long> {
  boolean existsByUserIdAndTopicId(Long userId, Long topicId);

  List<Subscription> findAllByUserId(Long userId);

  @Query("select s.topic.id from Subscription s where s.user.id = :userId")
  List<Long> findTopicIdsByUserId(Long userId);

  Optional<Subscription> findByUserIdAndTopicId(Long userId, Long topicId);

  void deleteByUserIdAndTopicId(Long userId, Long topicId);
}
