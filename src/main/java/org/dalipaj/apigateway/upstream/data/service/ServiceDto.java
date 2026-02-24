package org.dalipaj.apigateway.upstream.data.service;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.dalipaj.apigateway.common.validation.OnCreateGroup;
import org.dalipaj.apigateway.route.data.RouteDto;

import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@Getter
@Setter
public class ServiceDto {

    @JsonIgnore
    private Long id;

    @NotBlank(groups = OnCreateGroup.class)
    private String name;

    @Valid
    @NotEmpty(groups = OnCreateGroup.class)
    private List<RouteDto> routes;

    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private String applicationName;
}
