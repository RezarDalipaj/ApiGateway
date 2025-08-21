package org.dalipaj.apigateway.model.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class KeyValue {
    @NotBlank
    private String key;
    @NotNull
    private Object value;
    @NotBlank
    private String queryFrom;
}
