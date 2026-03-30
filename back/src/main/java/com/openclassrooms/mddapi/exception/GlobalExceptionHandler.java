package com.openclassrooms.mddapi.exception;

import com.openclassrooms.mddapi.dto.ApiErrorDto;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.ConstraintViolationException;
import java.time.LocalDateTime;
import java.util.Map;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationCredentialsNotFoundException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {
  private static final Logger LOGGER = LoggerFactory.getLogger(GlobalExceptionHandler.class);

  @ExceptionHandler(ResourceNotFoundException.class)
  public ResponseEntity<ApiErrorDto> handleNotFound(
      ResourceNotFoundException exception, HttpServletRequest request) {
    return buildResponse(HttpStatus.NOT_FOUND, exception.getMessage(), request, null);
  }

  @ExceptionHandler({MethodArgumentNotValidException.class, ConstraintViolationException.class})
  public ResponseEntity<ApiErrorDto> handleValidation(Exception exception, HttpServletRequest request) {
    Map<String, String> details = extractValidationDetails(exception);
    return buildResponse(HttpStatus.BAD_REQUEST, "Données invalides.", request, details);
  }

  @ExceptionHandler(DataIntegrityViolationException.class)
  public ResponseEntity<ApiErrorDto> handleDataIntegrity(
      DataIntegrityViolationException exception, HttpServletRequest request) {
    LOGGER.warn("Data integrity violation on {}: {}", request.getRequestURI(), exception.getMessage());
    return buildResponse(
        HttpStatus.CONFLICT,
        "Conflit de données. Vérifiez les contraintes d'unicité.",
        request,
        null);
  }

  @ExceptionHandler(IllegalArgumentException.class)
  public ResponseEntity<ApiErrorDto> handleBadRequest(
      IllegalArgumentException exception, HttpServletRequest request) {
    return buildResponse(HttpStatus.BAD_REQUEST, exception.getMessage(), request, null);
  }

  @ExceptionHandler(BadCredentialsException.class)
  public ResponseEntity<ApiErrorDto> handleBadCredentials(
      BadCredentialsException exception, HttpServletRequest request) {
    return buildResponse(HttpStatus.UNAUTHORIZED, "Identifiants invalides.", request, null);
  }

  @ExceptionHandler({AuthenticationException.class, AuthenticationCredentialsNotFoundException.class})
  public ResponseEntity<ApiErrorDto> handleUnauthorized(Exception exception, HttpServletRequest request) {
    return buildResponse(HttpStatus.UNAUTHORIZED, "Vous devez être authentifié.", request, null);
  }

  @ExceptionHandler(AccessDeniedException.class)
  public ResponseEntity<ApiErrorDto> handleAccessDenied(
      AccessDeniedException exception, HttpServletRequest request) {
    return buildResponse(HttpStatus.FORBIDDEN, "Accès refusé.", request, null);
  }

  @ExceptionHandler(Exception.class)
  public ResponseEntity<ApiErrorDto> handleGeneric(Exception exception, HttpServletRequest request) {
    LOGGER.error("Unhandled exception on {}", request.getRequestURI(), exception);
    return buildResponse(
        HttpStatus.INTERNAL_SERVER_ERROR, "Une erreur interne est survenue.", request, null);
  }

  private ResponseEntity<ApiErrorDto> buildResponse(
      HttpStatus status, String message, HttpServletRequest request, Map<String, String> details) {
    ApiErrorDto body =
        new ApiErrorDto(
            LocalDateTime.now(),
            status.value(),
            status.getReasonPhrase(),
            message,
            request.getRequestURI(),
            details);
    return ResponseEntity.status(status).body(body);
  }

  private Map<String, String> extractValidationDetails(Exception exception) {
    if (exception instanceof MethodArgumentNotValidException methodArgumentNotValidException) {
      return methodArgumentNotValidException.getBindingResult().getFieldErrors().stream()
          .collect(
              Collectors.toMap(
                  FieldError::getField,
                  fieldError -> fieldError.getDefaultMessage() == null ? "Valeur invalide" : fieldError.getDefaultMessage(),
                  (left, right) -> left));
    }

    if (exception instanceof ConstraintViolationException constraintViolationException) {
      return constraintViolationException.getConstraintViolations().stream()
          .collect(
              Collectors.toMap(
                  violation -> violation.getPropertyPath().toString(),
                  violation -> violation.getMessage() == null ? "Valeur invalide" : violation.getMessage(),
                  (left, right) -> left));
    }

    return Map.of();
  }
}
