package com.openclassrooms.mddapi.service;

import com.openclassrooms.mddapi.dto.TopicResponseDto;
import com.openclassrooms.mddapi.exception.ResourceNotFoundException;
import com.openclassrooms.mddapi.model.Subscription;
import com.openclassrooms.mddapi.model.Topic;
import com.openclassrooms.mddapi.model.User;
import com.openclassrooms.mddapi.repository.SubscriptionRepository;
import com.openclassrooms.mddapi.repository.TopicRepository;
import java.time.LocalDateTime;
import java.util.Set;
import java.util.stream.Collectors;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TopicService {

  private final TopicRepository topicRepository;
  private final SubscriptionRepository subscriptionRepository;
  private final CurrentUserService currentUserService;

  public TopicService(
      TopicRepository topicRepository,
      SubscriptionRepository subscriptionRepository,
      CurrentUserService currentUserService) {
    this.topicRepository = topicRepository;
    this.subscriptionRepository = subscriptionRepository;
    this.currentUserService = currentUserService;
  }

  public List<TopicResponseDto> getTopicsForCurrentUser() {
    User user = currentUserService.getCurrentUser();
    Set<Long> subscribedTopicIds =
        subscriptionRepository.findAllByUserId(user.getId()).stream()
            .map(subscription -> subscription.getTopic().getId())
            .collect(Collectors.toSet());

    return topicRepository.findAll().stream()
        .map(topic -> new TopicResponseDto(topic.getId(), topic.getName(), subscribedTopicIds.contains(topic.getId())))
        .toList();
  }

  public void subscribe(Long topicId) {
    User user = currentUserService.getCurrentUser();
    Topic topic =
        topicRepository.findById(topicId).orElseThrow(() -> new ResourceNotFoundException("Topic not found for id " + topicId));

    if (!subscriptionRepository.existsByUserIdAndTopicId(user.getId(), topicId)) {
      subscriptionRepository.save(
          Subscription.builder().user(user).topic(topic).createdAt(LocalDateTime.now()).build());
    }
  }

  public void unsubscribe(Long topicId) {
    User user = currentUserService.getCurrentUser();
    if (!topicRepository.existsById(topicId)) {
      throw new ResourceNotFoundException("Topic not found for id " + topicId);
    }
    subscriptionRepository.deleteByUserIdAndTopicId(user.getId(), topicId);
  }
}

