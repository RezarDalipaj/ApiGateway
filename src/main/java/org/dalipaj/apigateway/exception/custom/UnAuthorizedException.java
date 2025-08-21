package org.dalipaj.apigateway.exception.custom;

import org.dalipaj.apigateway.util.constants.Constants;

public class UnAuthorizedException extends Exception {
    public UnAuthorizedException(String message) {
        super(message);
    }
    public UnAuthorizedException() {
        super(Constants.UNAUTHORIZED_MESSAGE);
    }
}
