package com.openclassrooms.mddapi.repository;

import com.openclassrooms.mddapi.model.Subscription;
import java.util.Optional;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SubscriptionRepository extends JpaRepository<Subscription, Long> {
  boolean existsByUserIdAndTopicId(Long userId, Long topicId);

  List<Subscription> findAllByUserId(Long userId);

  Optional<Subscription> findByUserIdAndTopicId(Long userId, Long topicId);

  void deleteByUserIdAndTopicId(Long userId, Long topicId);
}

