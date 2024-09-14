package com.github.aymanalsagher.ams.controller.exception;

import com.github.aymanalsagher.ams.controller.exception.dto.ApiSubError;
import com.github.aymanalsagher.ams.controller.exception.dto.ApiValidationError;
import com.github.aymanalsagher.ams.service.shared.DomainError;
import com.github.aymanalsagher.ams.service.shared.DomainException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import java.net.URI;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.validator.internal.engine.path.PathImpl;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.lang.NonNull;
import org.springframework.transaction.TransactionSystemException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;
import org.springframework.web.util.UriComponentsBuilder;

@Slf4j
@RestControllerAdvice
public final class GlobalRestExceptionHandler extends ResponseEntityExceptionHandler {

  private final HttpHeaders headers;
  private static final String EXTENSIONS_SUB_ERRORS = "subErrors";

  GlobalRestExceptionHandler() {
    super();
    headers = new HttpHeaders();
    headers.setContentType(MediaType.APPLICATION_PROBLEM_JSON);
  }

  @Override
  protected ResponseEntity<Object> handleMethodArgumentNotValid(
      @NonNull MethodArgumentNotValidException ex,
      @NonNull HttpHeaders headers,
      @NonNull HttpStatusCode status,
      @NonNull WebRequest request) {
    log(ex);

    var body = createProblemDetail(ex, status, ex.getLocalizedMessage(), null, null, request);
    body.setType(DomainError.VALIDATION.getCode());
    body.setTitle(((HttpStatus) status).getReasonPhrase());
    var instance = getCurrentUri(request);
    body.setInstance(instance);
    body.setProperties(
        Map.of(EXTENSIONS_SUB_ERRORS, toErrors(ex.getBindingResult().getFieldErrors())));

    return handleExceptionInternal(
        ex, body, this.headers, getStatusFromDomainError(DomainError.VALIDATION), request);
  }

  @Override
  protected ResponseEntity<Object> handleHttpMessageNotReadable(
      HttpMessageNotReadableException ex,
      HttpHeaders headers,
      HttpStatusCode status,
      WebRequest request) {

    log(ex);

    var body = createProblemDetail(ex, status, ex.getLocalizedMessage(), null, null, request);
    body.setType(DomainError.VALIDATION.getCode());
    body.setTitle(((HttpStatus) status).getReasonPhrase());
    var instance = getCurrentUri(request);
    body.setInstance(instance);

    return handleExceptionInternal(
        ex, body, this.headers, getStatusFromDomainError(DomainError.VALIDATION), request);
  }

  @ExceptionHandler(value = ConstraintViolationException.class)
  ResponseEntity<Object> handle(
      ConstraintViolationException ex,
      HandlerMethod handlerMethod,
      WebRequest webRequest,
      HttpServletRequest servletRequest,
      HttpServletResponse servletResponse,
      HttpMethod httpMethod) {

    log(ex);

    var body =
        createProblemDetail(
            ex,
            getStatusFromDomainError(DomainError.VALIDATION),
            ex.getLocalizedMessage(),
            null,
            null,
            webRequest);
    body.setType(DomainError.VALIDATION.getCode());
    body.setTitle(
        ((HttpStatus) getStatusFromDomainError(DomainError.VALIDATION)).getReasonPhrase());
    var instance = getCurrentUri(webRequest);
    body.setInstance(instance);
    body.setProperties(Map.of(EXTENSIONS_SUB_ERRORS, toErrors(ex.getConstraintViolations())));

    return handleExceptionInternal(
        ex, body, this.headers, getStatusFromDomainError(DomainError.VALIDATION), webRequest);
  }

  @ExceptionHandler(value = TransactionSystemException.class)
  ResponseEntity<Object> handle(
      TransactionSystemException ex,
      HandlerMethod handlerMethod,
      WebRequest webRequest,
      HttpServletRequest servletRequest,
      HttpServletResponse servletResponse,
      HttpMethod httpMethod) {

    log(ex);

    if (ex.getRootCause() != null
        && ex.getRootCause() instanceof ConstraintViolationException rootCause) {
          return handle(
            rootCause, handlerMethod, webRequest, servletRequest, servletResponse, httpMethod);
      }

    log(ex);

    return handleAnyException(
        ex, handlerMethod, webRequest, servletRequest, servletResponse, httpMethod);
  }

  @ExceptionHandler(value = DomainException.class)
  ResponseEntity<Object> handleApplicationException(
      DomainException domainException,
      HandlerMethod handlerMethod,
      WebRequest webRequest,
      HttpServletRequest servletRequest,
      HttpServletResponse servletResponse,
      HttpMethod httpMethod) {

    log(domainException);

    var body =
        createProblemDetail(
            domainException,
            getStatusFromDomainError(domainException.getDomainError()),
            domainException.getLocalizedMessage(),
            null,
            null,
            webRequest);
    body.setType(domainException.getDomainError().getCode());
    body.setTitle(
        ((HttpStatus) getStatusFromDomainError(domainException.getDomainError()))
            .getReasonPhrase());
    var instance = getCurrentUri(webRequest);

    body.setInstance(instance);

    return handleExceptionInternal(
        domainException,
        body,
        this.headers,
        getStatusFromDomainError(domainException.getDomainError()),
        webRequest);
  }

  @ExceptionHandler(value = Exception.class)
  private ResponseEntity<Object> handleAnyException(
      Exception ex,
      HandlerMethod handlerMethod,
      WebRequest webRequest,
      HttpServletRequest servletRequest,
      HttpServletResponse servletResponse,
      HttpMethod httpMethod) {

    log(ex);

    var body =
        createProblemDetail(
            ex,
            getStatusFromDomainError(DomainError.UNEXPECTED),
            ex.getLocalizedMessage(),
            null,
            null,
            webRequest);
    body.setType(DomainError.UNEXPECTED.getCode());
    body.setTitle(
        ((HttpStatus) getStatusFromDomainError(DomainError.UNEXPECTED)).getReasonPhrase());
    var instance = getCurrentUri(webRequest);
    body.setInstance(instance);

    return handleExceptionInternal(
        ex, body, this.headers, getStatusFromDomainError(DomainError.UNEXPECTED), webRequest);
  }

  private void log(Exception ex) {
    String className = ex.getClass().getSimpleName();
    if (log.isDebugEnabled()) {
      log.warn("{} occurs with message: {}", className, ex.getMessage(), ex);
    } else {
      log.warn("{} occurs with message: {}", className, ex.getMessage());
    }
  }

  private List<ApiSubError> toErrors(List<FieldError> fieldErrors) {
    return fieldErrors.stream().map(this::toError).toList();
  }

  private List<ApiSubError> toErrors(Set<ConstraintViolation<?>> constraintViolations) {
    return constraintViolations.stream().map(this::toError).toList();
  }

  private ApiSubError toError(ConstraintViolation<?> cv) {
    return new ApiValidationError(
        cv.getRootBeanClass().getSimpleName(),
        ((PathImpl) cv.getPropertyPath()).getLeafNode().asString(),
        cv.getInvalidValue(),
        cv.getMessage());
  }

  private ApiSubError toError(FieldError fieldError) {
    return new ApiValidationError(
        fieldError.getObjectName(),
        fieldError.getField(),
        Objects.toString(fieldError.getRejectedValue(), null),
        fieldError.getDefaultMessage());
  }

  private static URI getCurrentUri(WebRequest request) {
    return UriComponentsBuilder.fromUriString(
            request.getDescription(false).substring("uri=".length()))
        .build(Map.of());
  }

  private HttpStatusCode getStatusFromDomainError(DomainError domainError) {
    return switch (domainError) {
      case RESOURCE_NOT_FOUND -> HttpStatus.NOT_FOUND;
      case VALIDATION -> HttpStatus.BAD_REQUEST;
      default -> HttpStatus.INTERNAL_SERVER_ERROR;
    };
  }
}
