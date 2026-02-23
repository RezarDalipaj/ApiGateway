package org.dalipaj.apigateway.rateLimit;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import org.dalipaj.apigateway.common.validation.OnCreateGroup;
import org.dalipaj.apigateway.common.validation.OnUpdateGroup;

@Builder
@Getter
@Setter
public class RateLimitDto {

    @JsonIgnore
    private Long id;

    @NotNull(groups = OnCreateGroup.class)
    private Integer perMinute;

    @NotNull(groups = OnCreateGroup.class)
    private Integer perHour;

    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    @NotEmpty(groups = OnCreateGroup.class)
    @Size(min = 6, max = 100, groups = {OnCreateGroup.class, OnUpdateGroup.class})
    private String apiKey;

    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private String applicationName;
}
