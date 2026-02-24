package org.dalipaj.apigateway.route.data;

import java.util.HashMap;
import java.util.Map;

final class Node {
    Map<String, Node> staticChildren = new HashMap<>();
    Node paramChild;
    String paramName;

    RouteDto route;
    boolean isRoute;

    boolean isLeaf() {
        return staticChildren.isEmpty() && paramChild == null;
    }
}
