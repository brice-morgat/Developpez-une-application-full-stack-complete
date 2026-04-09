package com.openclassrooms.mddapi.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.openclassrooms.mddapi.exception.ResourceNotFoundException;
import com.openclassrooms.mddapi.model.Topic;
import com.openclassrooms.mddapi.model.User;
import com.openclassrooms.mddapi.repository.SubscriptionRepository;
import com.openclassrooms.mddapi.repository.TopicRepository;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class TopicServiceTest {

  @Mock private TopicRepository topicRepository;
  @Mock private SubscriptionRepository subscriptionRepository;
  @Mock private CurrentAuthenticatedUserProvider currentAuthenticatedUserProvider;

  @InjectMocks private TopicService topicService;

  @Test
  void getTopicsForCurrentUser_shouldSetSubscribedFlag() {
    User user = User.builder().id(1L).username("alice").build();
    Topic t1 = Topic.builder().id(10L).name("Java").description("Java desc").build();
    Topic t2 = Topic.builder().id(11L).name("Angular").description("Angular desc").build();

    when(currentAuthenticatedUserProvider.getCurrentUser()).thenReturn(user);
    when(topicRepository.findAll()).thenReturn(List.of(t1, t2));
    when(subscriptionRepository.findTopicIdsByUserId(1L)).thenReturn(List.of(11L));

    var result = topicService.getTopicsForCurrentUser();

    assertThat(result).hasSize(2);
    assertThat(result.get(0).description()).isEqualTo("Java desc");
    assertThat(result.get(0).subscribed()).isFalse();
    assertThat(result.get(1).subscribed()).isTrue();
  }

  @Test
  void subscribe_shouldCreateWhenNotSubscribed() {
    User user = User.builder().id(1L).build();
    Topic topic = Topic.builder().id(2L).build();

    when(currentAuthenticatedUserProvider.getCurrentUser()).thenReturn(user);
    when(topicRepository.findById(2L)).thenReturn(Optional.of(topic));
    when(subscriptionRepository.existsByUserIdAndTopicId(1L, 2L)).thenReturn(false);

    topicService.subscribe(2L);

    verify(subscriptionRepository).save(org.mockito.ArgumentMatchers.any());
  }

  @Test
  void subscribe_shouldSkipWhenAlreadySubscribed() {
    User user = User.builder().id(1L).build();
    Topic topic = Topic.builder().id(2L).createdAt(LocalDateTime.now()).build();

    when(currentAuthenticatedUserProvider.getCurrentUser()).thenReturn(user);
    when(topicRepository.findById(2L)).thenReturn(Optional.of(topic));
    when(subscriptionRepository.existsByUserIdAndTopicId(1L, 2L)).thenReturn(true);

    topicService.subscribe(2L);

    verify(subscriptionRepository, never()).save(org.mockito.ArgumentMatchers.any());
  }

  @Test
  void unsubscribe_shouldFailWhenTopicMissing() {
    User user = User.builder().id(1L).build();
    when(currentAuthenticatedUserProvider.getCurrentUser()).thenReturn(user);
    when(topicRepository.existsById(44L)).thenReturn(false);

    assertThatThrownBy(() -> topicService.unsubscribe(44L))
        .isInstanceOf(ResourceNotFoundException.class)
        .hasMessageContaining("Thème introuvable");
  }
}
