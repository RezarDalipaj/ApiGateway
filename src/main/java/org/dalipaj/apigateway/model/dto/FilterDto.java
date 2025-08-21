package org.dalipaj.apigateway.model.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.List;

@Data
public class FilterDto {

    @NotNull
    private List<KeyValue> internalKeyValues;

    @NotNull
    private List<KeyValue> externalKeyValues;
}
