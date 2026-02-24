package org.dalipaj.apigateway.gateway.oauth;

import org.dalipaj.apigateway.route.data.oauth.OAuthDto;

public interface IOAuthService {

    String resolveAuthorization(OAuthDto oAuthDto);
}
