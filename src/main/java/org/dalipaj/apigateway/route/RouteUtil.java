package org.dalipaj.apigateway.route;

import lombok.experimental.UtilityClass;

import java.util.Arrays;

@UtilityClass
public class RouteUtil {

    public static final String PATH_SEPARATOR = "/";

    public String getMainPath(String fullPath) {
        var parts = getPartsFromPath(fullPath);
        return parts[0];
    }

    public String[] getPartsFromPath(String fullPath) {
        return Arrays.stream(fullPath.split(PATH_SEPARATOR))
                .filter(p -> !p.isBlank())
                .toArray(String[]::new);
    }
}
