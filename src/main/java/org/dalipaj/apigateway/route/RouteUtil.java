package org.dalipaj.apigateway.route;

import jakarta.servlet.http.HttpServletRequest;
import lombok.experimental.UtilityClass;
import org.apache.logging.log4j.util.Strings;

import java.util.Arrays;

@UtilityClass
public class RouteUtil {

    public static final String PATH_SEPARATOR = "/";

    public String[] getPartsFromPath(String fullPath) {
        return Arrays.stream(fullPath.split(PATH_SEPARATOR))
                .filter(p -> !p.isBlank())
                .toArray(String[]::new);
    }

    public String getPathWithQueryParams(HttpServletRequest request) {
        var path = request.getRequestURI();
        var queryString = request.getQueryString();

        return path + (Strings.isBlank(queryString) ? "" : "?" + queryString);
    }
}
