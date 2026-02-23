package org.dalipaj.apigateway.common.exception;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.dalipaj.apigateway.auth.UnAuthorizedException;
import org.dalipaj.apigateway.gateway.ApiCallException;
import org.dalipaj.apigateway.rateLimit.RateLimitException;
import org.dalipaj.apigateway.route.response.RouteResponseDto;
import org.dalipaj.apigateway.upstream.IUpstreamService;
import org.springframework.dao.InvalidDataAccessApiUsageException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.Map;


@ControllerAdvice
@RequiredArgsConstructor
@Slf4j
public class HttpExceptionHandler {

    private final IUpstreamService upstreamService;
    public static final String NOT_FOUND_MESSAGE = "Not found";

    @ExceptionHandler(NullPointerException.class)
    public ResponseEntity<ErrorDto> handleNullPointerException(NullPointerException nullException) {
        return buildError(nullException.getMessage() == null ? NOT_FOUND_MESSAGE
                : nullException.getMessage(), HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(BadRequestException.class)
    public ResponseEntity<ErrorDto> handleBadRequestException(BadRequestException badRequestException) {
        return getError(badRequestException, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(UnAuthorizedException.class)
    public ResponseEntity<ErrorDto> handleUnauthorizedException(UnAuthorizedException unAuthorizedException) {
        return getError(unAuthorizedException, HttpStatus.UNAUTHORIZED);
    }

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<ErrorDto> handleAccessDeniedException(AccessDeniedException accessDeniedException) {
        return getError(accessDeniedException, HttpStatus.FORBIDDEN);
    }

    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<ErrorDto> handleBadCredentialsException(BadCredentialsException badCredentialsException) {
        return getError(badCredentialsException, HttpStatus.UNAUTHORIZED);
    }

    @ExceptionHandler(RateLimitException.class)
    public ResponseEntity<ErrorDto> handleRateLimitException(RateLimitException rateLimitException) {
        return getError(rateLimitException, HttpStatus.TOO_MANY_REQUESTS);
    }

    @ExceptionHandler(ApiCallException.class)
    public ResponseEntity<RouteResponseDto> handleApiCallException(ApiCallException apiCallException) {
        var responseWithMetadata = apiCallException.getResponseWithMetadata();
        upstreamService.saveRouteResponseInCache(responseWithMetadata,
                apiCallException.getHttpMethod());

        return ResponseEntity.status(apiCallException.getStatus())
                .body(responseWithMetadata.getResponse());
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorDto> handleValidationException(MethodArgumentNotValidException validException) {
        Map<String, String> errors = new HashMap<>();

        validException.getBindingResult().getFieldErrors().forEach(error ->
                errors.put(error.getField(), error.getDefaultMessage())
        );

        var error = ErrorDto.builder()
                .message("Validation failed")
                .status(HttpStatus.BAD_REQUEST)
                .validationErrors(errors)
                .build();

        return ResponseEntity.badRequest().body(error);
    }

    @ExceptionHandler(InvalidDataAccessApiUsageException.class)
    public ResponseEntity<ErrorDto> handleFilterException(InvalidDataAccessApiUsageException apiUsageException) {
        return getError(apiUsageException, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(NoSuchAlgorithmException.class)
    public ResponseEntity<ErrorDto> handleNoSuchAlgorithmException(NoSuchAlgorithmException exception) {
        return getError(exception, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(UsernameNotFoundException.class)
    public ResponseEntity<ErrorDto> handleNoSuchAlgorithmException(UsernameNotFoundException exception) {
        return getError(exception, HttpStatus.UNAUTHORIZED);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorDto> handleException(Exception exception) {
        var status = getStatusOfException(exception);
        return buildError(status.toString(), status);
    }

    private HttpStatus getStatusOfException(Exception exception) {
        var cause = exception.getCause();
        if (cause instanceof NullPointerException)
            return HttpStatus.NOT_FOUND;
        if (cause instanceof UnAuthorizedException)
            return HttpStatus.UNAUTHORIZED;
        return HttpStatus.INTERNAL_SERVER_ERROR;
    }

    private ResponseEntity<ErrorDto> getError(Exception exception, HttpStatus status) {
       return buildError(exception.getMessage(), status);
    }

    private ResponseEntity<ErrorDto> buildError(String message, HttpStatus status) {
        log.warn("The following exception was thrown: {}, with message: {}", status, message);

        var error = ErrorDto.builder()
                .message(message)
                .status(status)
                .build();
        return  ResponseEntity.status(error.getStatus().value()).body(error);
    }
}
