package org.dalipaj.apigateway.gateway;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.logging.log4j.util.Strings;
import org.dalipaj.apigateway.route.response.RouteRedisResponseWithMetadata;
import org.dalipaj.apigateway.route.response.RouteResponseDto;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.ClientResponse;
import org.springframework.web.reactive.function.client.ExchangeFilterFunction;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;

@Component
@RequiredArgsConstructor
@Slf4j
public class WebClientExceptionHandler {

    private String pathWithQueryParams;

    public ExchangeFilterFunction createRequestHandler() {
        return ExchangeFilterFunction.ofRequestProcessor(clientRequest -> {
            var url = clientRequest.url();
            var queryParams = url.getQuery();
            pathWithQueryParams = Strings.isBlank(queryParams)
                    ? url.getPath()
                    : url.getPath() + "?" + queryParams;

            return Mono.just(clientRequest);
        });
    }

    public ExchangeFilterFunction createResponseErrorHandler() {
        return ExchangeFilterFunction.ofResponseProcessor(clientResponse -> {
            var statusCode = (HttpStatus) clientResponse.statusCode();
            // if the response is successful, return the response
            if (!statusCode.isError())
                return Mono.just(clientResponse);

            return handleApiError(clientResponse, statusCode);
        });
    }

    private Mono<ClientResponse> handleApiError(ClientResponse clientResponse, HttpStatus statusCode) {
        // if the response is empty, or it doesn't map, throw api call exception
        var errorResponse = RouteRedisResponseWithMetadata.builder()
                .exactPath(pathWithQueryParams)
                .lastCached(LocalDateTime.now())
                .response(RouteResponseDto.builder()
                        .status((HttpStatus) clientResponse.statusCode())
                        .headers(clientResponse.headers().asHttpHeaders())
                        .build())
                .build();

        return clientResponse.bodyToMono(Object.class)
                .onErrorResume(error -> {
                    var cannotMapMessage = "Cannot map api call error response";
                    log.error(cannotMapMessage, error);
                    errorResponse.getResponse().setData(cannotMapMessage);
                    throw new ApiCallException(error.getMessage(), statusCode, errorResponse);
                })
                // otherwise, return the error body
                .flatMap(errorBody -> {
                    errorResponse.getResponse().setData(errorBody);
                    return Mono.error(new ApiCallException("Error", statusCode, errorResponse));
                });
    }
}

