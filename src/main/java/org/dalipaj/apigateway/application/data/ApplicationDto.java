package org.dalipaj.apigateway.application.data;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;
import org.dalipaj.apigateway.auth.data.LoginDto;

@Getter
@Setter
public class ApplicationDto extends LoginDto {

    @JsonIgnore
    private Long id;

    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private String role;
}
