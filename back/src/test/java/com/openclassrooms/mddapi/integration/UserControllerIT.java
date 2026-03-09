package com.openclassrooms.mddapi.integration;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.JsonNode;
import com.openclassrooms.mddapi.model.Topic;
import java.nio.charset.StandardCharsets;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;

class UserControllerIT extends BaseIT {

  @Test
  void givenAuthenticatedUser_whenGetProfile_thenCurrentUserIsReturned() throws Exception {
    String email = uniqueEmail("user-me");
    String username = uniqueUsername("user_me");
    String token = registerAndLogin(email, username);

    String response =
        mockMvc
            .perform(get("/api/users/me").header("Authorization", bearer(token)))
            .andExpect(status().isOk())
            .andReturn()
            .getResponse()
            .getContentAsString(StandardCharsets.UTF_8);

    JsonNode json = parseBody(response);
    assertThat(json.get("email").asText()).isEqualTo(email);
    assertThat(json.get("username").asText()).isEqualTo(username);
  }

  @Test
  void givenAuthenticatedUser_whenUpdateProfile_thenUpdatedValuesAreReturned() throws Exception {
    String email = uniqueEmail("profile");
    String token = registerAndLogin(email, uniqueUsername("profile_user"));

    String updateBody =
        """
        {
          "email": "updated_%s",
          "username": "profile_user_updated",
          "password": "newpassword123"
        }
        """
            .formatted(email);

    String response =
        mockMvc
            .perform(
                put("/api/users/me")
                    .header("Authorization", bearer(token))
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(updateBody))
            .andExpect(status().isOk())
            .andReturn()
            .getResponse()
            .getContentAsString(StandardCharsets.UTF_8);

    JsonNode json = parseBody(response);
    assertThat(json.get("username").asText()).isEqualTo("profile_user_updated");
    assertThat(json.get("email").asText()).startsWith("updated_");
  }

  @Test
  void givenUserSubscribedToTopic_whenGetProfile_thenSubscriptionsContainTopic() throws Exception {
    Topic topic = createTopic("Profile Topic");
    String email = uniqueEmail("user-sub");
    String token = registerAndLogin(email, uniqueUsername("user_sub"));

    mockMvc
        .perform(post("/api/topics/{topicId}/subscribe", topic.getId()).header("Authorization", bearer(token)))
        .andExpect(status().isNoContent());

    String response =
        mockMvc
            .perform(get("/api/users/me").header("Authorization", bearer(token)))
            .andExpect(status().isOk())
            .andReturn()
            .getResponse()
            .getContentAsString(StandardCharsets.UTF_8);

    JsonNode json = parseBody(response);
    assertThat(json.get("subscriptions").toString()).contains(topic.getId().toString());
  }
}
