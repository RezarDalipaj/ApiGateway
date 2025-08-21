package org.dalipaj.apigateway.util.security;

import org.dalipaj.apigateway.util.constants.Constants;
import lombok.experimental.UtilityClass;

@UtilityClass
public final class PasswordUtil {

    private final String salt = Constants.SALT;
    private final Integer halfSaltLength = salt.length()/2;
    private final String halfSalt = salt.substring(0, halfSaltLength);

    public static String getSaltedPassword(String password) {
        return halfSalt.concat(password).concat(salt);
    }
}
