package org.dalipaj.apigateway.route;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public enum RouteAuthType {
    OAUTH("OAUTH"),          // OAuth 2.0
    API_KEY("API_KEY"),       // API Key
    BASIC("BASIC"),         // Basic Authentication
    JWT("JWT"),           // JSON Web Token
    NONE("NONE");          // No authentication

    private final String value;
}
