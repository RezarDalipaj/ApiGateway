package org.dalipaj.apigateway.route.dto;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

public class RouteTrie {

    private final Node root = new Node();

    private List<String> tokenize(String path) {
        return Arrays.stream(path.split("/"))
                .filter(s -> !s.isBlank())
                .toList();
    }

    private boolean isParam(String segment) {
        return segment.startsWith("{") && segment.endsWith("}");
    }

    private String extractParamName(String segment) {
        return segment.substring(1, segment.length() - 1);
    }

    public void insert(RouteDto routeDto) {
        Node current = root;

        for (String segment : tokenize(routeDto.getPath())) {

            if (isParam(segment)) {
                if (current.paramChild == null) {
                    current.paramChild = new Node();
                    current.paramChild.paramName = extractParamName(segment);
                }
                current = current.paramChild;
            } else {
                current = current.staticChildren
                        .computeIfAbsent(segment, k -> new Node());
            }
        }

        current.isRoute = true;
        current.route = routeDto;
    }

    public Optional<RouteDto> findExact(String path) {
        Node node = traverse(path);
        if (node != null && node.isRoute) {
            return Optional.of(node.route);
        }
        return Optional.empty();
    }

    private Node traverse(String path) {
        Node current = root;

        for (String segment : tokenize(path)) {
            Node staticChild = current.staticChildren.get(segment);

            if (staticChild != null) {
                current = staticChild;
            } else if (current.paramChild != null) {
                current = current.paramChild;
            } else {
                return null;
            }
        }

        return current;
    }

    public Optional<RouteDto> match(String requestPath) {

        Node current = root;

        for (String segment : tokenize(requestPath)) {

            // Static match first
            Node staticChild = current.staticChildren.get(segment);
            if (staticChild != null) {
                current = staticChild;
                continue;
            }

            // Param match
            if (current.paramChild != null) {
                current = current.paramChild;
                continue;
            }

            return Optional.empty();
        }

        if (current.isRoute) {
            return Optional.of(current.route);
        }

        return Optional.empty();
    }

    public boolean delete(String path) {
        return delete(root, tokenize(path), 0);
    }

    private boolean delete(Node current, List<String> segments, int index) {

        if (index == segments.size()) {
            if (!current.isRoute) return false;

            current.isRoute = false;
            current.route = null;

            return current.isLeaf();
        }

        String segment = segments.get(index);
        Node next;

        if (isParam(segment) && current.paramChild != null) {
            next = current.paramChild;
        } else {
            next = current.staticChildren.get(segment);
        }

        if (next == null) return false;

        boolean shouldDeleteChild = delete(next, segments, index + 1);

        if (shouldDeleteChild) {
            if (isParam(segment)) {
                current.paramChild = null;
            } else {
                current.staticChildren.remove(segment);
            }

            return !current.isRoute && current.isLeaf();
        }

        return false;
    }
}