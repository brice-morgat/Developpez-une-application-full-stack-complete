package com.openclassrooms.mddapi.exception;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.ConstraintViolationException;
import java.util.Set;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.web.servlet.resource.NoResourceFoundException;

@ExtendWith(MockitoExtension.class)
class GlobalExceptionHandlerTest {

  private final GlobalExceptionHandler handler = new GlobalExceptionHandler();

  @Mock private HttpServletRequest request;

  @Test
  void handleNotFound_shouldReturn404() {
    when(request.getRequestURI()).thenReturn("/api/posts/99");

    var response = handler.handleNotFound(new ResourceNotFoundException("not found"), request);

    assertThat(response.getStatusCode().value()).isEqualTo(404);
    assertThat(response.getBody().message()).isEqualTo("not found");
  }

  @Test
  void handleNoResourceFound_shouldReturn404() {
    when(request.getRequestURI()).thenReturn("/swagger-ui.html");

    var response =
        handler.handleNoResourceFound(new NoResourceFoundException(HttpMethod.GET, "/swagger-ui.html"), request);

    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
    assertThat(response.getBody().message()).isEqualTo("Ressource introuvable.");
  }

  @Test
  void handleForbiddenOperation_shouldReturn403() {
    when(request.getRequestURI()).thenReturn("/api/posts");

    var response =
        handler.handleForbiddenOperation(
            new ForbiddenOperationException("Vous devez être abonné au thème pour publier un article."), request);

    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.FORBIDDEN);
    assertThat(response.getBody().message())
        .isEqualTo("Vous devez être abonné au thème pour publier un article.");
  }

  @Test
  void handleValidation_shouldReturn400() {
    when(request.getRequestURI()).thenReturn("/api/posts");

    var response = handler.handleValidation(new ConstraintViolationException("invalid", Set.of()), request);

    assertThat(response.getStatusCode().value()).isEqualTo(HttpStatus.BAD_REQUEST.value());
    assertThat(response.getBody().message()).isEqualTo("Données invalides.");
  }

  @Test
  void handleDataIntegrity_shouldReturn409() {
    when(request.getRequestURI()).thenReturn("/api/users/me");

    var response = handler.handleDataIntegrity(new DataIntegrityViolationException("constraint"), request);

    assertThat(response.getStatusCode().value()).isEqualTo(409);
  }

  @Test
  void handleBadRequest_shouldReturn400() {
    when(request.getRequestURI()).thenReturn("/api/users/me");

    var response = handler.handleBadRequest(new IllegalArgumentException("bad"), request);

    assertThat(response.getStatusCode().value()).isEqualTo(400);
    assertThat(response.getBody().message()).isEqualTo("bad");
  }

  @Test
  void handleBadCredentials_shouldReturn401() {
    when(request.getRequestURI()).thenReturn("/api/auth/login");

    var response = handler.handleBadCredentials(new BadCredentialsException("x"), request);

    assertThat(response.getStatusCode().value()).isEqualTo(401);
    assertThat(response.getBody().message()).isEqualTo("Identifiants invalides.");
  }

  @Test
  void handleGeneric_shouldReturn500() {
    when(request.getRequestURI()).thenReturn("/api/test");

    var response = handler.handleGeneric(new RuntimeException("boom"), request);

    assertThat(response.getStatusCode().value()).isEqualTo(500);
    assertThat(response.getBody().message()).isEqualTo("Une erreur interne est survenue.");
  }
}
