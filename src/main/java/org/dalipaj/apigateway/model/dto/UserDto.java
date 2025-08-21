package org.dalipaj.apigateway.model.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import org.dalipaj.apigateway.model.dto.request.LoginDto;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserDto extends LoginDto {

    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private Long id;
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private String role;
    @Email
    @NotBlank
    private String email;
}
