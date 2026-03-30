package com.openclassrooms.mddapi.integration;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.JsonNode;
import java.nio.charset.StandardCharsets;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;

class RegisterPasswordValidationIT extends BaseIT {

  @Test
  void givenWeakPassword_whenRegister_thenBadRequestWithPasswordDetail() throws Exception {
    String body =
        """
        {
          "email": "%s",
          "username": "%s",
          "password": "password123"
        }
        """
            .formatted(uniqueEmail("weak-password"), uniqueUsername("weak_password"));

    var result =
        mockMvc
            .perform(post("/api/auth/register").contentType(MediaType.APPLICATION_JSON).content(body))
            .andExpect(status().isBadRequest())
            .andReturn();

    JsonNode json = parseBody(result.getResponse().getContentAsString(StandardCharsets.UTF_8));
    assertThat(json.get("message").asText()).isEqualTo("Donn\u00e9es invalides.");
    assertThat(json.get("details").get("password").asText())
        .contains("au moins une minuscule, une majuscule, un chiffre et un caract\u00e8re sp\u00e9cial");
  }
}
