package com.openclassrooms.mddapi.integration;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.openclassrooms.mddapi.model.Post;
import com.openclassrooms.mddapi.model.Topic;
import com.openclassrooms.mddapi.model.User;
import com.openclassrooms.mddapi.repository.CommentRepository;
import com.openclassrooms.mddapi.repository.PostRepository;
import com.openclassrooms.mddapi.repository.SubscriptionRepository;
import com.openclassrooms.mddapi.repository.TopicRepository;
import com.openclassrooms.mddapi.repository.UserRepository;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class ControllerIntegrationTest {

  @Autowired private MockMvc mockMvc;
  @Autowired private ObjectMapper objectMapper;
  @Autowired private UserRepository userRepository;
  @Autowired private TopicRepository topicRepository;
  @Autowired private PostRepository postRepository;
  @Autowired private SubscriptionRepository subscriptionRepository;
  @Autowired private CommentRepository commentRepository;

  @BeforeEach
  void cleanDatabase() {
    commentRepository.deleteAll();
    postRepository.deleteAll();
    subscriptionRepository.deleteAll();
    topicRepository.deleteAll();
    userRepository.deleteAll();
  }

  @Test
  void givenNewUser_whenRegister_thenTokenAndUserAreReturned() throws Exception {
    String email = "user+" + UUID.randomUUID() + "@test.com";

    String body =
        """
        {
          "email": "%s",
          "username": "bdd_user",
          "password": "password123"
        }
        """
            .formatted(email);

    var result =
        mockMvc
            .perform(post("/api/auth/register").contentType(MediaType.APPLICATION_JSON).content(body))
            .andExpect(status().isOk())
            .andReturn();

    JsonNode json = parseBody(result.getResponse().getContentAsString(StandardCharsets.UTF_8));
    assertThat(json.get("token").asText()).isNotBlank();
    assertThat(json.get("user").get("email").asText()).isEqualTo(email);
    assertThat(userRepository.findByEmail(email)).isPresent();
  }

  @Test
  void givenRegisteredUser_whenLogin_thenJwtIsReturned() throws Exception {
    String email = "login+" + UUID.randomUUID() + "@test.com";
    registerUser(email, "login_user");

    String loginBody =
        """
        {
          "identifier": "%s",
          "password": "password123"
        }
        """
            .formatted(email);

    var result =
        mockMvc
            .perform(post("/api/auth/login").contentType(MediaType.APPLICATION_JSON).content(loginBody))
            .andExpect(status().isOk())
            .andReturn();

    JsonNode json = parseBody(result.getResponse().getContentAsString(StandardCharsets.UTF_8));
    assertThat(json.get("token").asText()).isNotBlank();
  }

  @Test
  void givenAuthenticatedUserAndTopic_whenSubscribeAndUnsubscribe_thenStateIsPersisted() throws Exception {
    Topic topic = createTopic("Java Integration");
    String token = registerAndLogin("sub+" + UUID.randomUUID() + "@test.com", "sub_user");

    mockMvc
        .perform(post("/api/topics/{topicId}/subscribe", topic.getId()).header("Authorization", "Bearer " + token))
        .andExpect(status().isNoContent());

    String meResponse =
        mockMvc
            .perform(get("/api/users/me").header("Authorization", "Bearer " + token))
            .andExpect(status().isOk())
            .andReturn()
            .getResponse()
            .getContentAsString(StandardCharsets.UTF_8);

    JsonNode meJson = parseBody(meResponse);
    assertThat(meJson.get("subscriptions").toString()).contains(topic.getId().toString());

    mockMvc
        .perform(delete("/api/topics/{topicId}/subscribe", topic.getId()).header("Authorization", "Bearer " + token))
        .andExpect(status().isNoContent());

    String meAfterUnsubscribe =
        mockMvc
            .perform(get("/api/users/me").header("Authorization", "Bearer " + token))
            .andExpect(status().isOk())
            .andReturn()
            .getResponse()
            .getContentAsString(StandardCharsets.UTF_8);

    JsonNode meAfter = parseBody(meAfterUnsubscribe);
    assertThat(meAfter.get("subscriptions").toString()).doesNotContain(topic.getId().toString());
  }

  @Test
  void givenSubscribedTopicAndPost_whenGetFeed_thenPostIsReturned() throws Exception {
    Topic topic = createTopic("Spring Tests");
    String token = registerAndLogin("feed+" + UUID.randomUUID() + "@test.com", "feed_user");

    mockMvc
        .perform(post("/api/topics/{topicId}/subscribe", topic.getId()).header("Authorization", "Bearer " + token))
        .andExpect(status().isNoContent());

    User author = userRepository.findByUsername("feed_user").orElseThrow();
    postRepository.save(
        Post.builder()
            .title("Integration Feed Post")
            .content("Content from integration test")
            .author(author)
            .topic(topic)
            .createdAt(LocalDateTime.now())
            .build());

    String feedResponse =
        mockMvc
            .perform(get("/api/feed").param("sort", "desc").header("Authorization", "Bearer " + token))
            .andExpect(status().isOk())
            .andReturn()
            .getResponse()
            .getContentAsString(StandardCharsets.UTF_8);

    JsonNode feedJson = parseBody(feedResponse);
    assertThat(feedJson.isArray()).isTrue();
    assertThat(feedJson.toString()).contains("Integration Feed Post");
  }

  @Test
  void givenExistingPost_whenGetDetailAndAddComment_thenCommentIsPersisted() throws Exception {
    Topic topic = createTopic("Comments Topic");
    String token = registerAndLogin("comment+" + UUID.randomUUID() + "@test.com", "comment_user");

    mockMvc
        .perform(post("/api/topics/{topicId}/subscribe", topic.getId()).header("Authorization", "Bearer " + token))
        .andExpect(status().isNoContent());

    User author = userRepository.findByUsername("comment_user").orElseThrow();
    Post post =
        postRepository.save(
            Post.builder()
                .title("Post Detail Integration")
                .content("Post body")
                .author(author)
                .topic(topic)
                .createdAt(LocalDateTime.now())
                .build());

    mockMvc
        .perform(get("/api/posts/{postId}", post.getId()).header("Authorization", "Bearer " + token))
        .andExpect(status().isOk());

    String commentBody =
        """
        {
          "content": "Integration comment"
        }
        """;

    mockMvc
        .perform(
            post("/api/posts/{postId}/comments", post.getId())
                .header("Authorization", "Bearer " + token)
                .contentType(MediaType.APPLICATION_JSON)
                .content(commentBody))
        .andExpect(status().isOk());

    String detailResponse =
        mockMvc
            .perform(get("/api/posts/{postId}", post.getId()).header("Authorization", "Bearer " + token))
            .andExpect(status().isOk())
            .andReturn()
            .getResponse()
            .getContentAsString(StandardCharsets.UTF_8);

    JsonNode detailJson = parseBody(detailResponse);
    assertThat(detailJson.get("comments").toString()).contains("Integration comment");
  }

  @Test
  void givenAuthenticatedUser_whenUpdateProfile_thenUpdatedValuesAreReturned() throws Exception {
    String email = "profile+" + UUID.randomUUID() + "@test.com";
    String token = registerAndLogin(email, "profile_user");

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
                    .header("Authorization", "Bearer " + token)
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

  private void registerUser(String email, String username) throws Exception {
    String body =
        """
        {
          "email": "%s",
          "username": "%s",
          "password": "password123"
        }
        """
            .formatted(email, username);

    mockMvc
        .perform(post("/api/auth/register").contentType(MediaType.APPLICATION_JSON).content(body))
        .andExpect(status().isOk());
  }

  private String registerAndLogin(String email, String username) throws Exception {
    registerUser(email, username);

    String loginBody =
        """
        {
          "identifier": "%s",
          "password": "password123"
        }
        """
            .formatted(email);

    String loginResponse =
        mockMvc
            .perform(post("/api/auth/login").contentType(MediaType.APPLICATION_JSON).content(loginBody))
            .andExpect(status().isOk())
            .andReturn()
            .getResponse()
            .getContentAsString(StandardCharsets.UTF_8);

    return parseBody(loginResponse).get("token").asText();
  }

  private Topic createTopic(String name) {
    return topicRepository.save(
        Topic.builder().name(name).description("desc").createdAt(LocalDateTime.now()).build());
  }

  private JsonNode parseBody(String body) throws Exception {
    return objectMapper.readTree(body);
  }
}
