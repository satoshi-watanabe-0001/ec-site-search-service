package com.ecsite.search.exception;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.core.MethodParameter;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;

class GlobalExceptionHandlerTest {

  private GlobalExceptionHandler exceptionHandler;

  @BeforeEach
  void setUp() {
    exceptionHandler = new GlobalExceptionHandler();
  }

  @Test
  void handleValidationExceptions_ReturnsBadRequest() throws NoSuchMethodException {
    BindingResult bindingResult = mock(BindingResult.class);
    FieldError fieldError = new FieldError("searchRequest", "page", "Page must be at least 1");
    when(bindingResult.getFieldErrors()).thenReturn(Collections.singletonList(fieldError));

    java.lang.reflect.Method method =
        GlobalExceptionHandlerTest.class.getDeclaredMethod(
            "handleValidationExceptions_ReturnsBadRequest");
    MethodParameter methodParameter = new MethodParameter(method, -1);

    MethodArgumentNotValidException ex =
        new MethodArgumentNotValidException(methodParameter, bindingResult);

    ResponseEntity<Map<String, Object>> response = exceptionHandler.handleValidationExceptions(ex);

    assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    Map<String, Object> body = response.getBody();
    assertNotNull(body);
    assertEquals("error", body.get("status"));
    assertEquals("Validation failed", body.get("message"));
    assertTrue(body.containsKey("errors"));
    assertTrue(body.containsKey("timestamp"));

    @SuppressWarnings("unchecked")
    List<Map<String, String>> errors = (List<Map<String, String>>) body.get("errors");
    assertEquals(1, errors.size());
    assertEquals("page", errors.get(0).get("field"));
    assertEquals("Page must be at least 1", errors.get(0).get("message"));
  }

  @Test
  void handleIOException_ReturnsServiceUnavailable() {
    IOException ex = new IOException("Elasticsearch connection failed");

    ResponseEntity<Map<String, Object>> response = exceptionHandler.handleIOException(ex);

    assertEquals(HttpStatus.SERVICE_UNAVAILABLE, response.getStatusCode());
    Map<String, Object> body = response.getBody();
    assertNotNull(body);
    assertEquals("error", body.get("status"));
    assertEquals("Search service temporarily unavailable", body.get("message"));
    assertTrue(body.containsKey("timestamp"));
  }

  @Test
  void handleGenericException_ReturnsInternalServerError() {
    Exception ex = new RuntimeException("Unexpected error");

    ResponseEntity<Map<String, Object>> response = exceptionHandler.handleGenericException(ex);

    assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
    Map<String, Object> body = response.getBody();
    assertNotNull(body);
    assertEquals("error", body.get("status"));
    assertEquals("An unexpected error occurred", body.get("message"));
    assertTrue(body.containsKey("timestamp"));
  }
}
