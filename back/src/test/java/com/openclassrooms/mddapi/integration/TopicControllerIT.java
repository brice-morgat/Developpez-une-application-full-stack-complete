package com.openclassrooms.mddapi.integration;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.JsonNode;
import com.openclassrooms.mddapi.model.Topic;
import com.openclassrooms.mddapi.model.User;
import java.nio.charset.StandardCharsets;
import org.junit.jupiter.api.Test;

class TopicControllerIT extends BaseIT {

  @Test
  void givenAuthenticatedUser_whenListTopics_thenTopicsWithDescriptionsAndSubscriptionStateAreReturned()
      throws Exception {
    Topic topic = createTopic("Java Integration");
    String email = uniqueEmail("topic-list");
    String username = uniqueUsername("topic_list");
    String token = registerAndLogin(email, username);

    mockMvc
        .perform(post("/api/topics/{topicId}/subscribe", topic.getId()).header("Authorization", bearer(token)))
        .andExpect(status().isNoContent());

    String response =
        mockMvc
            .perform(get("/api/topics").header("Authorization", bearer(token)))
            .andExpect(status().isOk())
            .andReturn()
            .getResponse()
            .getContentAsString(StandardCharsets.UTF_8);

    JsonNode json = parseBody(response);
    assertThat(json.isArray()).isTrue();
    assertThat(json.toString()).contains("Java Integration");
    assertThat(json.toString()).contains("desc");
    assertThat(json.toString()).contains("subscribed");
  }

  @Test
  void givenAuthenticatedUserAndTopic_whenSubscribe_thenSubscriptionStateIsPersisted() throws Exception {
    Topic topic = createTopic("Java Integration");
    String email = uniqueEmail("topic-sub");
    String username = uniqueUsername("topic_sub");
    String token = registerAndLogin(email, username);
    User user = userRepository.findByEmail(email).orElseThrow();

    mockMvc
        .perform(post("/api/topics/{topicId}/subscribe", topic.getId()).header("Authorization", bearer(token)))
        .andExpect(status().isNoContent());

    assertThat(subscriptionRepository.existsByUserIdAndTopicId(user.getId(), topic.getId())).isTrue();
  }

  @Test
  void givenAuthenticatedUserAndTopic_whenUnsubscribe_thenSubscriptionStateIsRemoved() throws Exception {
    Topic topic = createTopic("Java Integration");
    String email = uniqueEmail("topic-unsub");
    String username = uniqueUsername("topic_unsub");
    String token = registerAndLogin(email, username);
    User user = userRepository.findByEmail(email).orElseThrow();

    mockMvc
        .perform(post("/api/topics/{topicId}/subscribe", topic.getId()).header("Authorization", bearer(token)))
        .andExpect(status().isNoContent());

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
