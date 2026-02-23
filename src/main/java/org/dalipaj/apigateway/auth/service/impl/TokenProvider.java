package org.dalipaj.apigateway.auth.service.impl;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.UnsupportedJwtException;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.security.SignatureException;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.dalipaj.apigateway.application.mapper.ApplicationMapper;
import org.dalipaj.apigateway.auth.TokenUtil;
import org.dalipaj.apigateway.auth.config.JwtProperties;
import org.dalipaj.apigateway.auth.UnAuthorizedException;
import org.dalipaj.apigateway.application.ApplicationDto;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.ZonedDateTime;
import java.util.Date;
import java.util.Optional;
import java.util.UUID;

@Slf4j
@Component
@RequiredArgsConstructor
public class TokenProvider {

    private final ApplicationMapper applicationMapper;
    private final JwtProperties jwtProperties;
    public static final String TOKEN_TYPE = "JWT";

    private byte[] getSigningKey() {
        return jwtProperties.getSecret().getBytes();
    }

    @Transactional
    public String generateAccessToken(Authentication authentication) {
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        var appDto = applicationMapper.toAppDto(userDetails);
        return buildToken(appDto);
    }

    @Transactional
    public String buildToken(ApplicationDto applicationDto) {
        return Jwts.builder()
                .setHeaderParam("type", TOKEN_TYPE)
                .signWith(Keys.hmacShaKeyFor(getSigningKey()), SignatureAlgorithm.HS512)
                .setExpiration(Date.from(ZonedDateTime.now().plusMinutes(getMinutesFromBoolean()).toInstant()))
                .setIssuedAt(Date.from(ZonedDateTime.now().toInstant()))
                .setId(UUID.randomUUID().toString())
                .setSubject(applicationDto.getName())
                .claim("role", applicationDto.getRole())
                .compact();
    }

    private Integer getMinutesFromBoolean () {
        return jwtProperties.getAccessMinutes();
    }

    public Optional<Jws<Claims>> validateTokenAndGetJws(String token) {
        try {
            Jws<Claims> jws = Jwts.parserBuilder()
                    .setSigningKey(getSigningKey())
                    .build()
                    .parseClaimsJws(token);

            return Optional.of(jws);

        } catch (ExpiredJwtException exception) {
            log.error("Request to parse expired JWT failed : {}", exception.getMessage());
        } catch (UnsupportedJwtException exception) {
            log.error("Request to parse unsupported JWT failed : {}", exception.getMessage());
        } catch (MalformedJwtException exception) {
            log.error("Request to parse invalid JWT failed : {}", exception.getMessage());
        } catch (SignatureException exception) {
            log.error("Request to parse JWT with invalid signature failed : {}", exception.getMessage());
        } catch (IllegalArgumentException exception) {
            log.error("Request to parse empty or null JWT failed : {}", exception.getMessage());
        }
        return Optional.empty();
    }

    private Jws<Claims> getClaimsFromAccessToken(String token) throws UnAuthorizedException {
        var claims = validateTokenAndGetJws(token);
        return claims.orElseThrow(UnAuthorizedException::new);
    }

    public String getAppNameFromRequest(HttpServletRequest request) throws UnAuthorizedException {
        var token = TokenUtil.getTokenFromRequest(request);
        return getAppNameFromAccessToken(token);
    }

    public String getAppNameFromAccessToken(String token) throws UnAuthorizedException {
        var claims = getClaimsFromAccessToken(token);
        return claims.getBody().getSubject();
    }
}

