package com.openclassrooms.mddapi.integration;

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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
abstract class BaseIT {

  @Autowired protected MockMvc mockMvc;
  @Autowired protected ObjectMapper objectMapper;
  @Autowired protected UserRepository userRepository;
  @Autowired protected TopicRepository topicRepository;
  @Autowired protected PostRepository postRepository;
  @Autowired protected SubscriptionRepository subscriptionRepository;
  @Autowired protected CommentRepository commentRepository;

  @BeforeEach
  void cleanDatabase() {
    commentRepository.deleteAll();
    postRepository.deleteAll();
    subscriptionRepository.deleteAll();
    topicRepository.deleteAll();
    userRepository.deleteAll();
  }

  protected String uniqueEmail(String prefix) {
    return prefix + "+" + UUID.randomUUID() + "@test.com";
  }

  protected String uniqueUsername(String prefix) {
    return prefix + "_" + UUID.randomUUID().toString().replace("-", "").substring(0, 8);
  }

  protected String bearer(String token) {
    return "Bearer " + token;
  }

  protected void registerUser(String email, String username) throws Exception {
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
        .andExpect(status().isCreated());
  }

  protected String registerAndLogin(String email, String username) throws Exception {
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

  protected Topic createTopic(String name) {
    return topicRepository.save(
        Topic.builder().name(name).description("desc").createdAt(LocalDateTime.now()).build());
  }

  protected Post createPost(String title, String content, User author, Topic topic) {
    return postRepository.save(
        Post.builder()
            .title(title)
            .content(content)
            .author(author)
            .topic(topic)
            .createdAt(LocalDateTime.now())
            .build());
  }

  protected JsonNode parseBody(String body) throws Exception {
    return objectMapper.readTree(body);
  }
}
