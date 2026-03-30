package com.openclassrooms.mddapi.integration;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.JsonNode;
import java.nio.charset.StandardCharsets;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;

class AuthControllerIT extends BaseIT {

  @Test
  void givenNewUser_whenRegister_thenTokenAndUserAreReturned() throws Exception {
    String email = uniqueEmail("auth-register");
    String username = uniqueUsername("auth_register");

    String body =
        """
        {
          "email": "%s",
          "username": "%s",
          "password": "%s"
        }
        """
            .formatted(email, username, VALID_PASSWORD);

    var result =
        mockMvc
            .perform(post("/api/auth/register").contentType(MediaType.APPLICATION_JSON).content(body))
            .andExpect(status().isCreated())
            .andReturn();

    JsonNode json = parseBody(result.getResponse().getContentAsString(StandardCharsets.UTF_8));
    assertThat(json.get("token").asText()).isNotBlank();
    assertThat(json.get("user").get("email").asText()).isEqualTo(email);
    assertThat(userRepository.findByEmail(email)).isPresent();
  }

  @Test
  void givenRegisteredUser_whenLogin_thenJwtIsReturned() throws Exception {
    String email = uniqueEmail("auth-login");
    String username = uniqueUsername("auth_login");
    registerUser(email, username);

    String loginBody =
        """
        {
          "identifier": "%s",
          "password": "%s"
        }
        """
            .formatted(email, VALID_PASSWORD);

    var result =
        mockMvc
            .perform(post("/api/auth/login").contentType(MediaType.APPLICATION_JSON).content(loginBody))
            .andExpect(status().isOk())
            .andReturn();

    JsonNode json = parseBody(result.getResponse().getContentAsString(StandardCharsets.UTF_8));
    assertThat(json.get("token").asText()).isNotBlank();
  }
}
