package org.dalipaj.apigateway.route.data.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.io.Serializable;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@Getter
@Setter
@ToString
public class RouteResponseKey implements Serializable {

    private String exactPath;
    private String allowedSortedHeaders;
    private String httpMethod;
}
