package org.dalipaj.apigateway.rateLimit;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Builder
@Getter
@Setter
public class RateLimitDto {

    @JsonIgnore
    private Long id;

    @NotNull
    private Integer perMinute;

    @NotNull
    private Integer perHour;

    @NotEmpty
    @Size(min = 6, max = 100)
    private String apiKey;
}
