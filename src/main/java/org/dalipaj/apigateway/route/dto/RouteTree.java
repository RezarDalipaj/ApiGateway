package org.dalipaj.apigateway.route.dto;

import org.dalipaj.apigateway.route.RouteUtil;

public class RouteTree {

    private final RouteNode root = new RouteNode();

    public void save(RouteDto route) {
        String[] parts = RouteUtil.getPartsFromPath(route.getPath());

        RouteNode current = root;
        for (String part : parts) {
            current = current.getChildren().computeIfAbsent(part, k -> new RouteNode());
        }
        current.setRoute(route);
    }

    public RouteDto find(String path) {
        String[] parts = RouteUtil.getPartsFromPath(path);

        RouteNode current = root;
        RouteDto lastMatched = null;

        for (String part : parts) {
            if (!current.getChildren().containsKey(part))
                break;

            current = current.getChildren().get(part);
            if (current.getRoute() != null) {
                lastMatched = current.getRoute(); // longest-prefix match
            }
        }

        if (lastMatched == null)
            throw new NullPointerException("Route with path : " + path + " not found");

        return lastMatched;
    }

    public boolean delete(String path) {
        String[] parts = RouteUtil.getPartsFromPath(path);
        return deleteRecursive(root, parts, 0);
    }

    private boolean deleteRecursive(RouteNode current, String[] parts, int index) {
        if (index == parts.length) {
            if (current.getRoute() == null) {
                return false; // nothing to delete
            }
            current.setRoute(null);
            return current.getChildren().isEmpty(); // prune if no children
        }

        String part = parts[index];
        RouteNode child = current.getChildren().get(part);
        if (child == null)
            return false;

        boolean shouldDeleteChild = deleteRecursive(child, parts, index + 1);

        if (shouldDeleteChild) {
            current.getChildren().remove(part);
            return current.getChildren().isEmpty() && current.getRoute() == null;
        }

        return false;
    }

}

