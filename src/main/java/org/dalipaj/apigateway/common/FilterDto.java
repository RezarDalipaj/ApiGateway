package org.dalipaj.apigateway.common;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class FilterDto {
    @NotBlank
    private String key;
    @NotNull
    private Object value;
    @NotBlank
    private FilterOperator operator;
}
