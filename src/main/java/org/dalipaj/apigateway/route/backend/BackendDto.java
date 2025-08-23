package org.dalipaj.apigateway.route.backend;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Builder
@Getter
@Setter
public class BackendDto {

    @NotEmpty
    private String url;

    @NotNull
    private Integer weight;
}
