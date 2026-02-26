package org.dalipaj.apigateway.gateway.webclient;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.logging.log4j.util.Strings;
import org.dalipaj.apigateway.gateway.GatewayService;
import org.dalipaj.apigateway.route.data.response.RouteRedisResponseWithMetadata;
import org.dalipaj.apigateway.route.data.response.RouteResponseDto;
import org.dalipaj.apigateway.route.data.response.RouteResponseKey;
import org.springframework.http.HttpRequest;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.ClientResponse;
import org.springframework.web.reactive.function.client.ExchangeFilterFunction;
import reactor.core.publisher.Mono;

import static org.dalipaj.apigateway.route.RouteUtil.QUERY_PARAM_START;

@Component
@RequiredArgsConstructor
@Slf4j
public class WebClientExceptionHandler {

    private String getPath(HttpRequest request) {
            var url = request.getURI();
            var queryParams = url.getQuery();

            return Strings.isBlank(queryParams)
                    ? url.getPath()
                    : url.getPath() + QUERY_PARAM_START + queryParams;

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
        var request = clientResponse.request();

        var errorResponse = RouteRedisResponseWithMetadata.builder()
                .key(RouteResponseKey.builder()
                        .exactPath(getPath(request))
                        .allowedSortedHeaders(GatewayService.allowListAndSortHeaders(request.getHeaders()))
                        .httpMethod(request.getMethod().toString())
                        .build())
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
                    throw new ApiCallException(error.getMessage(), statusCode, request.getMethod(), errorResponse);
                })
                // otherwise, return the error body
                .flatMap(errorBody -> {
                    errorResponse.getResponse().setData(errorBody);
                    return Mono.error(new ApiCallException("Error", statusCode, request.getMethod(), errorResponse));
                });
    }
}

