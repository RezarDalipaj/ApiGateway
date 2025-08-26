package org.dalipaj.apigateway.rateLimit;

public class RateLimitException extends Exception{

    public RateLimitException(String message) {
        super(message);
    }
}
