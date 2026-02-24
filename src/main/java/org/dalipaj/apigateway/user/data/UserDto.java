package org.dalipaj.apigateway.user.data;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;
import org.dalipaj.apigateway.auth.data.LoginDto;

import java.util.Set;

@Getter
@Setter
public class UserDto extends LoginDto {

    @JsonIgnore
    private Long id;

    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private String role;

    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private Set<String> scopes;
}
