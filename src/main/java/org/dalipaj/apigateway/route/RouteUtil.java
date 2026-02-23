package org.dalipaj.apigateway.route;

import jakarta.servlet.http.HttpServletRequest;
import lombok.experimental.UtilityClass;
import org.apache.logging.log4j.util.Strings;

@UtilityClass
public class RouteUtil {

    private static final char SLASH = '/';
    private static final String QUERY_PARAM_START = "?";

    public String getPathWithQueryParams(HttpServletRequest request) {
        var path = request.getRequestURI();
        var queryString = request.getQueryString();

        return path + (Strings.isBlank(queryString) ? Strings.EMPTY : QUERY_PARAM_START + queryString);
    }

    public String removeServiceName(String path) {
        if (Strings.isBlank(path)) {
            return path;
        }

        int startIndex;
        if (path.charAt(0) == SLASH) {
            // start at the 2nd slash
            startIndex = path.indexOf(SLASH, 1);
        } else {
            // start at the 1st slash
            startIndex = path.indexOf(SLASH);
        }

        if (startIndex == -1) {
            return path;
        }

        return path.substring(startIndex);
    }
}
