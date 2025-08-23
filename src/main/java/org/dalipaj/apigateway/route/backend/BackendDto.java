package org.dalipaj.apigateway.route.backend;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Builder
@Getter
@Setter
public class BackendDto {

    private String url;
    private Integer weight;
}
