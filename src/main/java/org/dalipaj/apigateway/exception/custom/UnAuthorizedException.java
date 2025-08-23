package org.dalipaj.apigateway.exception.custom;

public class UnAuthorizedException extends Exception {
    public static final String UNAUTHORIZED_MESSAGE = "Unauthorized!";

    public UnAuthorizedException(String message) {
        super(message);
    }
    public UnAuthorizedException() {
        super(UNAUTHORIZED_MESSAGE);
    }
}
