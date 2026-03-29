package com.openclassrooms.mddapi.integration;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.JsonNode;
import com.openclassrooms.mddapi.model.Post;
import com.openclassrooms.mddapi.model.Topic;
import com.openclassrooms.mddapi.model.User;
import java.nio.charset.StandardCharsets;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;

class PostControllerIT extends BaseIT {

  @Test
  void givenAuthenticatedUserSubscribedToTopic_whenCreatePost_thenCreatedPostIsReturned() throws Exception {
    Topic topic = createTopic("Create Post Topic");
    String email = uniqueEmail("create-post");
    String username = uniqueUsername("create_post");
    String token = registerAndLogin(email, username);

    mockMvc
        .perform(post("/api/topics/{topicId}/subscribe", topic.getId()).header("Authorization", bearer(token)))
        .andExpect(status().isNoContent());

    String body =
        """
        {
          "topicId": %d,
          "title": "Created by integration",
          "content": "Body from integration test"
        }
        """
            .formatted(topic.getId());

    String response =
        mockMvc
            .perform(
                post("/api/posts")
                    .header("Authorization", bearer(token))
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(body))
            .andExpect(status().isCreated())
            .andReturn()
            .getResponse()
            .getContentAsString(StandardCharsets.UTF_8);

    JsonNode json = parseBody(response);
    assertThat(json.get("title").asText()).isEqualTo("Created by integration");
    assertThat(json.get("topic").get("description").asText()).isEqualTo("desc");
    assertThat(postRepository.findAll()).hasSize(1);
  }

  @Test
  void givenAuthenticatedUserNotSubscribedToTopic_whenCreatePost_thenForbiddenIsReturned() throws Exception {
    Topic topic = createTopic("Forbidden Topic");
    String email = uniqueEmail("forbidden-post");
    String username = uniqueUsername("forbidden_post");
    String token = registerAndLogin(email, username);

    String body =
        """
        {
          "topicId": %d,
          "title": "Blocked post",
          "content": "Body from integration test"
        }
        """
            .formatted(topic.getId());

    String response =
        mockMvc
            .perform(
                post("/api/posts")
                    .header("Authorization", bearer(token))
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(body))
            .andExpect(status().isForbidden())
            .andReturn()
            .getResponse()
            .getContentAsString(StandardCharsets.UTF_8);

    JsonNode json = parseBody(response);
    assertThat(json.get("message").asText()).contains("publier un article");
    assertThat(postRepository.findAll()).isEmpty();
  }

  @Test
  void givenSubscribedTopicAndPost_whenGetFeed_thenPostIsReturned() throws Exception {
    Topic topic = createTopic("Spring Tests");
    String email = uniqueEmail("feed");
    String username = uniqueUsername("feed_user");
    String token = registerAndLogin(email, username);

    mockMvc
        .perform(post("/api/topics/{topicId}/subscribe", topic.getId()).header("Authorization", bearer(token)))
        .andExpect(status().isNoContent());

    User author = userRepository.findByEmail(email).orElseThrow();
    createPost("Integration Feed Post", "Content from integration test", author, topic);

    String feedResponse =
        mockMvc
            .perform(get("/api/feed").param("sort", "desc").header("Authorization", bearer(token)))
            .andExpect(status().isOk())
            .andReturn()
            .getResponse()
            .getContentAsString(StandardCharsets.UTF_8);

    JsonNode feedJson = parseBody(feedResponse);
    assertThat(feedJson.isArray()).isTrue();
    assertThat(feedJson.toString()).contains("Integration Feed Post");
    assertThat(feedJson.toString()).contains("desc");
  }

  @Test
  void givenExistingPost_whenGetDetail_thenPostIsReturned() throws Exception {
    Topic topic = createTopic("Detail Topic");
    String email = uniqueEmail("detail");
    String username = uniqueUsername("detail_user");
    String token = registerAndLogin(email, username);

    mockMvc
        .perform(post("/api/topics/{topicId}/subscribe", topic.getId()).header("Authorization", bearer(token)))
        .andExpect(status().isNoContent());

    User author = userRepository.findByEmail(email).orElseThrow();
    Post post = createPost("Post Detail Integration", "Post body", author, topic);

    String detailResponse =
        mockMvc
            .perform(get("/api/posts/{postId}", post.getId()).header("Authorization", bearer(token)))
            .andExpect(status().isOk())
            .andReturn()
            .getResponse()
            .getContentAsString(StandardCharsets.UTF_8);

    JsonNode detailJson = parseBody(detailResponse);
    assertThat(detailJson.get("title").asText()).isEqualTo("Post Detail Integration");
    assertThat(detailJson.get("topic").get("description").asText()).isEqualTo("desc");
  }

  @Test
  void givenExistingPost_whenAddComment_thenCommentIsPersisted() throws Exception {
    Topic topic = createTopic("Comments Topic");
    String email = uniqueEmail("comment");
    String username = uniqueUsername("comment_user");
    String token = registerAndLogin(email, username);

    mockMvc
        .perform(post("/api/topics/{topicId}/subscribe", topic.getId()).header("Authorization", bearer(token)))
        .andExpect(status().isNoContent());

    User author = userRepository.findByEmail(email).orElseThrow();
    Post post = createPost("Post Detail Integration", "Post body", author, topic);

    String commentBody =
        """
        {
          "content": "Integration comment"
        }
        """;

    mockMvc
        .perform(
            post("/api/posts/{postId}/comments", post.getId())
                .header("Authorization", bearer(token))
                .contentType(MediaType.APPLICATION_JSON)
                .content(commentBody))
        .andExpect(status().isCreated());

    String detailResponse =
        mockMvc
            .perform(get("/api/posts/{postId}", post.getId()).header("Authorization", bearer(token)))
            .andExpect(status().isOk())
            .andReturn()
            .getResponse()
            .getContentAsString(StandardCharsets.UTF_8);

    JsonNode detailJson = parseBody(detailResponse);
    assertThat(detailJson.get("comments").toString()).contains("Integration comment");
  }
}


