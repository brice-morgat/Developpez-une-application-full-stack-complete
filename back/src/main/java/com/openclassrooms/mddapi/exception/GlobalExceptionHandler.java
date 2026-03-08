package com.openclassrooms.mddapi.exception;

import com.openclassrooms.mddapi.dto.ApiErrorDto;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.ConstraintViolationException;
import java.time.LocalDateTime;
import java.util.Map;
import java.util.stream.Collectors;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

  @ExceptionHandler(ResourceNotFoundException.class)
  public ResponseEntity<ApiErrorDto> handleNotFound(
      ResourceNotFoundException exception, HttpServletRequest request) {
    return buildResponse(HttpStatus.NOT_FOUND, exception.getMessage(), request, null);
  }

  @ExceptionHandler({MethodArgumentNotValidException.class, ConstraintViolationException.class})
  public ResponseEntity<ApiErrorDto> handleValidation(Exception exception, HttpServletRequest request) {
    Map<String, String> details = extractValidationDetails(exception);
    return buildResponse(HttpStatus.BAD_REQUEST, "Validation failed", request, details);
  }

  @ExceptionHandler(DataIntegrityViolationException.class)
  public ResponseEntity<ApiErrorDto> handleDataIntegrity(
      DataIntegrityViolationException exception, HttpServletRequest request) {
    return buildResponse(
        HttpStatus.CONFLICT,
        "Data integrity violation. Check unique constraints and relationships.",
        request,
        null);
  }

  @ExceptionHandler(IllegalArgumentException.class)
  public ResponseEntity<ApiErrorDto> handleBadRequest(
      IllegalArgumentException exception, HttpServletRequest request) {
    return buildResponse(HttpStatus.BAD_REQUEST, exception.getMessage(), request, null);
  }

  @ExceptionHandler(Exception.class)
  public ResponseEntity<ApiErrorDto> handleGeneric(Exception exception, HttpServletRequest request) {
    return buildResponse(
        HttpStatus.INTERNAL_SERVER_ERROR, "Unexpected server error", request, null);
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
                  fieldError -> fieldError.getDefaultMessage() == null ? "Invalid value" : fieldError.getDefaultMessage(),
                  (left, right) -> left));
    }

    if (exception instanceof ConstraintViolationException constraintViolationException) {
      return constraintViolationException.getConstraintViolations().stream()
          .collect(
              Collectors.toMap(
                  violation -> violation.getPropertyPath().toString(),
                  violation -> violation.getMessage() == null ? "Invalid value" : violation.getMessage(),
                  (left, right) -> left));
    }

    return Map.of();
  }
}

