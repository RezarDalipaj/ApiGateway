package org.dalipaj.apigateway.exception.custom;

public class BadRequestException extends Exception {
    public BadRequestException(String message) {
        super(message);
    }
}
