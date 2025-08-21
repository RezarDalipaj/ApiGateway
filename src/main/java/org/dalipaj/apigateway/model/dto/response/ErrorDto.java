package org.dalipaj.apigateway.model.dto.response;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import org.springframework.http.HttpStatus;

import java.util.Map;

@Builder
@Getter
@Setter
public class ErrorDto {
    private HttpStatus status;
    private String message;
    private Map<String, String> errors;
}
