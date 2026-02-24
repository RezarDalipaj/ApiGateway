package org.dalipaj.apigateway.common.filter;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class FilterDto {

    @NotBlank
    private String key;

    @NotNull
    private Object value;

    @NotNull
    private FilterOperator operator;
}
