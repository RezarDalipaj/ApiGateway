package org.dalipaj.apigateway.rateLimit.service;

public class RateLimitException extends Exception{

    public RateLimitException(String message) {
        super(message);
    }
}
