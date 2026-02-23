package org.dalipaj.apigateway.auth;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;
import org.dalipaj.apigateway.common.validation.OnCreateGroup;
import org.dalipaj.apigateway.common.validation.OnUpdateGroup;

import java.io.Serializable;

@Getter
@Setter
public class LoginDto implements Serializable {

    @NotBlank
    private String name;

    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    @NotBlank(groups = OnCreateGroup.class)
    @Size(min = 6, max = 30, groups = {OnCreateGroup.class, OnUpdateGroup.class})
    private String password;
}
