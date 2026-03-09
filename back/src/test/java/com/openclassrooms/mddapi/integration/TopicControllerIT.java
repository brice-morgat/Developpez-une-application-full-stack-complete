package com.openclassrooms.mddapi.integration;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.openclassrooms.mddapi.model.Topic;
import com.openclassrooms.mddapi.model.User;
import org.junit.jupiter.api.Test;

class TopicControllerIT extends BaseIT {

  @Test
  void givenAuthenticatedUserAndTopic_whenSubscribeAndUnsubscribe_thenSubscriptionStateIsPersisted()
      throws Exception {
    Topic topic = createTopic("Java Integration");
    String email = uniqueEmail("topic-sub");
    String username = uniqueUsername("topic_sub");
    String token = registerAndLogin(email, username);
    User user = userRepository.findByEmail(email).orElseThrow();

    mockMvc
        .perform(post("/api/topics/{topicId}/subscribe", topic.getId()).header("Authorization", bearer(token)))
        .andExpect(status().isNoContent());

    assertThat(subscriptionRepository.existsByUserIdAndTopicId(user.getId(), topic.getId())).isTrue();

    mockMvc
        .perform(delete("/api/topics/{topicId}/subscribe", topic.getId()).header("Authorization", bearer(token)))
        .andExpect(status().isNoContent());

    assertThat(subscriptionRepository.existsByUserIdAndTopicId(user.getId(), topic.getId())).isFalse();
  }

  @Test
  void givenUnknownTopic_whenSubscribe_thenNotFoundIsReturned() throws Exception {
    String token = registerAndLogin(uniqueEmail("topic-404"), uniqueUsername("topic_404"));

    mockMvc
        .perform(post("/api/topics/{topicId}/subscribe", 999999L).header("Authorization", bearer(token)))
        .andExpect(status().isNotFound());
  }
}
