package org.dalipaj.apigateway.util.security;

import org.dalipaj.apigateway.util.constants.Constants;
import jakarta.servlet.http.HttpServletRequest;
import lombok.experimental.UtilityClass;
import org.springframework.util.StringUtils;

import java.util.Optional;

@UtilityClass
public final class TokenUtil {

    public static String getTokenFromRequest(HttpServletRequest request) {
        return getJwtFromRequest(request).orElseThrow();
    }

    public static Optional<String> getJwtFromRequest(HttpServletRequest request) {
        String tokenHeader = request.getHeader(Constants.Token.TOKEN_HEADER);
        if (StringUtils.hasText(tokenHeader) && tokenHeader.startsWith(Constants.Token.TOKEN_PREFIX)) {
            return Optional.of(tokenHeader.replace(Constants.Token.TOKEN_PREFIX, ""));
        }
        return Optional.empty();
    }
}
