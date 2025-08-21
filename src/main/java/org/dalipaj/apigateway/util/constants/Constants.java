package org.dalipaj.apigateway.util.constants;

import lombok.experimental.UtilityClass;

@UtilityClass
public final class Constants {

    public static final String NOT_FOUND_MESSAGE = "Not found";
    public static final String UNAUTHORIZED_MESSAGE = "Unauthorized!";
    public static final String SALT = "fouwrhvn";

    @UtilityClass
    public static final class Token {
        public static final String TOKEN_TYPE = "JWT";
        public static final String TOKEN_HEADER = "Authorization";
        public static final String TOKEN_PREFIX = "Bearer ";
    }
}
