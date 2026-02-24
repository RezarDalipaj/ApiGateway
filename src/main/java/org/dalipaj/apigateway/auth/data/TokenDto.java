package org.dalipaj.apigateway.auth.data;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class TokenDto {

    private String accessToken;
}
