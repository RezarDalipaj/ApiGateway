package org.dalipaj.apigateway.repository.redis;

import org.dalipaj.apigateway.model.entity.Route;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class RadixRouter {

    static final class Node {
        // literal children: segment -> node
        Map<String, Node> literal = new HashMap<>();

        // special children
        Node param;     // {var}
        Node star;      // *
        Node global;      // **

        // routes terminating here (by method)
        // method "*" holds the fallback for any method.
        Map<String, Route> routes = new HashMap<>();
    }

    private final Node root = new Node();

    public void clear() {
        root.literal.clear();
        root.param = root.star = root.global = null;
        root.routes.clear();
    }

    public void add(Route route) {
        if (route == null || !route.isEnabled())
            return;

        String pattern = normalize(route.getPathPattern());
        String[] segments = split(pattern);
        Node current = root;

        for (int i = 0; i < segments.length; i++) {
            String segment = segments[i];
            boolean last = (i == segments.length - 1);

            if ("**".equals(segment)) {
                if (current.global == null)
                    current.global = new Node();

                current = current.global;
                if (last)
                    putRoute(current, route);
                // "**" at non-last positions still allowed: to continue descent
            } else if ("*".equals(segment)) {
                if (current.star == null)
                    current.star = new Node();

                current = current.star;

                if (last) putRoute(current, route);
            } else if (isParam(segment)) {
                if (current.param == null)
                    current.param = new Node();

                current = current.param;

                if (last)
                    putRoute(current, route);
            } else {
                current = current.literal.computeIfAbsent(segment, k -> new Node());
                if (last)
                    putRoute(current, route);
            }
        }
    }

    private void putRoute(Node node, Route route) {
        String method = route.getHttpMethod() == null ? "*" : route.getHttpMethod().toUpperCase(Locale.ROOT);
        node.routes.put(method, pickMoreSpecific(node.routes.get(method), route));
        // also set "*" bucket if it helps (optional)
        if (!"*".equals(method)) {
            node.routes.put("*", pickMoreSpecific(node.routes.get("*"), route));
        }
    }

    // Prefer more specific routes: more literal segments > param > star > glob, then longer pattern
    private Route pickMoreSpecific(Route a, Route b) {
        if (a == null) return b;
        int sa = specificityScore(a.getPathPattern());
        int sb = specificityScore(b.getPathPattern());
        if (sb > sa) return b;
        if (sb < sa) return a;
        // tie-breaker: longer pattern wins
        return b.getPathPattern().length() >= a.getPathPattern().length() ? b : a;
    }

    private int specificityScore(String pattern) {
        String[] segments = split(normalize(pattern));
        int score = 0;
        for (String s : segments) {
            if ("**".equals(s)) score += 0;
            else if ("*".equals(s)) score += 1;
            else if (isParam(s)) score += 2;
            else score += 3; // literal
        }
        return score;
    }

    public MatchResult match(String method, String path) {
        String m = method == null ? "*" : method.toUpperCase(Locale.ROOT);
        String[] segments = split(normalize(path));
        Deque<State> stack = new ArrayDeque<>();
        stack.push(new State(root, 0));

        Route best = null;

        while (!stack.isEmpty()) {
            State state = stack.pop();
            Node node = state.node;
            int i = state.i;

            // If we consumed all segments, consider routes at this node
            if (i == segments.length) {
                Route route = pick(node.routes.get(m), node.routes.get("*"));
                best = pick(best, route);
                // "**" can match empty tail: if there is a glob child, it can still match
                if (node.global != null) {
                    Route gr = pick(node.global.routes.get(m), node.global.routes.get("*"));
                    best = pick(best, gr);
                }
                continue;
            }

            String seg = segments[i];

            // try literal
            Node lit = node.literal.get(seg);
            if (lit != null) stack.push(new State(lit, i + 1));

            // try param
            if (node.param != null) stack.push(new State(node.param, i + 1));

            // try star
            if (node.star != null) stack.push(new State(node.star, i + 1));

            // try glob (consume 0+ segments)
            if (node.global != null) {
                // 1) consume none
                stack.push(new State(node.global, i));
                // 2) consume this seg
                stack.push(new State(node.global, i + 1));
            }
        }

        return best == null ? MatchResult.miss() : MatchResult.hit(best);
    }

    private static class State {
        Node node;
        int i;

        State(Node node, int i) {
            this.node = node;
            this.i = i;
        }
    }

    private Route pick(Route a, Route b) {
        if (a == null) return b;
        if (b == null) return a;
        return pickMoreSpecific(a, b);
    }

    private boolean isParam(String s) {
        return s.startsWith("{") && s.endsWith("}");
    }

    private String normalize(String p) {
        if (p == null || p.isEmpty()) return "/";
        String t = p.startsWith("/") ? p : "/" + p;
        // collapse multiple slashes
        return t.replaceAll("/{2,}", "/");
    }

    private String[] split(String p) {
        if ("/".equals(p)) return new String[0];
        if (p.startsWith("/")) p = p.substring(1);
        return p.isEmpty() ? new String[0] : p.split("/");
    }

    // Result wrapper
    public static final class MatchResult {
        public final boolean found;
        public final Route route;

        private MatchResult(boolean f, Route r) {
            found = f;
            route = r;
        }

        public static MatchResult hit(Route r) {
            return new MatchResult(true, r);
        }

        public static MatchResult miss() {
            return new MatchResult(false, null);
        }
    }
}

