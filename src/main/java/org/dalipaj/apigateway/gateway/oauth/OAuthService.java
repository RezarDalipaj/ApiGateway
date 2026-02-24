package org.dalipaj.apigateway.gateway.oauth;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.logging.log4j.util.Strings;
import org.dalipaj.apigateway.route.data.oauth.OAuthDto;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.reactive.function.client.WebClient;

import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
@RequiredArgsConstructor
@Slf4j
public class OAuthService implements IOAuthService {

    private final WebClient webClient;
    private final Map<String, CachedToken> tokenCache = new ConcurrentHashMap<>();
    private static final String LINE = "|";
    private static final String GRANT_TYPE = "grant_type";
    private static final String CLIENT_CREDENTIALS = "client_credentials";
    private static final String SCOPE = "scope";
    private static final String COLON = ":";
    private static final String BASIC_PREFIX = "Basic ";

    @Override
    public String resolveAuthorization(OAuthDto oauth) {
        if (oauth == null)
            return null;

        String tokenEndpoint = oauth.getTokenEndpoint();
        String clientId = oauth.getClientId();
        String clientSecret = oauth.getClientSecret();
        String scope = oauth.getScope();

        if (Strings.isBlank(tokenEndpoint) || Strings.isBlank(clientId) || Strings.isBlank(clientSecret))
            return null;

        String cacheKey = buildCacheKey(tokenEndpoint, clientId, scope);
        CachedToken cached = tokenCache.get(cacheKey);

        if (cached != null && !cached.isExpired())
            return cached.accessToken();

        MultiValueMap<String, String> form = new LinkedMultiValueMap<>();
        form.add(GRANT_TYPE, CLIENT_CREDENTIALS);

        if (!Strings.isBlank(scope))
            form.add(SCOPE, scope);

        return webClient
                .post()
                .uri(tokenEndpoint)
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .header(HttpHeaders.AUTHORIZATION, basicAuth(clientId, clientSecret))
                .bodyValue(form)
                .retrieve()
                .bodyToMono(OAuthTokenDto.class)
                .map(this::toCachedToken)
                .doOnNext(token -> tokenCache.put(cacheKey, token))
                .map(CachedToken::accessToken)
                .block();
    }

    private record CachedToken(String accessToken, long expiresAtMillis) {
        boolean isExpired() {
            return expiresAtMillis <= System.currentTimeMillis();
        }
    }

    private CachedToken toCachedToken(OAuthTokenDto oAuthToken) {
        Object expiresIn = oAuthToken.getExpiresIn();

        long expiresInSeconds = 0L;
        if (expiresIn instanceof Number n) {
            expiresInSeconds = n.longValue();
        } else if (expiresIn != null) {
            try {
                expiresInSeconds = Long.parseLong(expiresIn.toString());
            } catch (NumberFormatException ignored) {
                log.info("Cannot parse expires_in value: {}, defaulting to 0", expiresIn);
            }
        }

        long safetySkewSeconds = 30L;
        long expiresAtMillis = System.currentTimeMillis() + Math.max(0L, (expiresInSeconds - safetySkewSeconds)) * 1000L;

        if (Strings.isBlank(oAuthToken.getAccessToken()))
            return new CachedToken(Strings.EMPTY, 0L);

        return new CachedToken(oAuthToken.getAccessToken(), expiresAtMillis);
    }

    private String basicAuth(String clientId, String clientSecret) {
        String raw = clientId + COLON + clientSecret;
        String b64 = Base64.getEncoder().encodeToString(raw.getBytes(StandardCharsets.UTF_8));
        return BASIC_PREFIX + b64;
    }

    private String buildCacheKey(String tokenEndpoint, String clientId, String scope) {
        return tokenEndpoint + LINE + clientId + LINE + (scope == null ? Strings.EMPTY : scope);
    }
}
