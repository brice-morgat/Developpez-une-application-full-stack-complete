package com.openclassrooms.mddapi.service;

import com.openclassrooms.mddapi.dto.TopicResponseDto;
import com.openclassrooms.mddapi.exception.ResourceNotFoundException;
import com.openclassrooms.mddapi.model.Subscription;
import com.openclassrooms.mddapi.model.Topic;
import com.openclassrooms.mddapi.model.User;
import com.openclassrooms.mddapi.repository.SubscriptionRepository;
import com.openclassrooms.mddapi.repository.TopicRepository;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
public class TopicService {

  private final TopicRepository topicRepository;
  private final SubscriptionRepository subscriptionRepository;
  private final CurrentAuthenticatedUserProvider currentAuthenticatedUserProvider;

  public TopicService(
      TopicRepository topicRepository,
      SubscriptionRepository subscriptionRepository,
      CurrentAuthenticatedUserProvider currentAuthenticatedUserProvider) {
    this.topicRepository = topicRepository;
    this.subscriptionRepository = subscriptionRepository;
    this.currentAuthenticatedUserProvider = currentAuthenticatedUserProvider;
  }

  public List<TopicResponseDto> getTopicsForCurrentUser() {
    User user = currentAuthenticatedUserProvider.getCurrentUser();
    Set<Long> subscribedTopicIds =
        subscriptionRepository.findTopicIdsByUserId(user.getId()).stream().collect(Collectors.toSet());

    return topicRepository.findAll().stream()
        .map(
            topic ->
                new TopicResponseDto(
                    topic.getId(),
                    topic.getName(),
                    topic.getDescription(),
                    subscribedTopicIds.contains(topic.getId())))
        .toList();
  }

  @Transactional
  public void subscribe(Long topicId) {
    User user = currentAuthenticatedUserProvider.getCurrentUser();
    Topic topic =
        topicRepository
            .findById(topicId)
            .orElseThrow(() -> new ResourceNotFoundException("Thème introuvable."));

    if (!subscriptionRepository.existsByUserIdAndTopicId(user.getId(), topicId)) {
      subscriptionRepository.save(
          Subscription.builder().user(user).topic(topic).createdAt(LocalDateTime.now()).build());
    }
  }

  @Transactional
  public void unsubscribe(Long topicId) {
    User user = currentAuthenticatedUserProvider.getCurrentUser();
    if (!topicRepository.existsById(topicId)) {
      throw new ResourceNotFoundException("Thème introuvable.");
    }
    subscriptionRepository.deleteByUserIdAndTopicId(user.getId(), topicId);
  }
}
