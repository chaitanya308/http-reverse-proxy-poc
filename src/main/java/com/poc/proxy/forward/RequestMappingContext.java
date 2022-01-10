package com.poc.proxy.forward;

import java.util.regex.Pattern;

import static java.util.regex.Pattern.compile;

public class RequestMappingContext {

    private String name;
    private Pattern pathRegex;
    private PathTemplate rewritePath;
    private RoutingContext routingContext;

    public RequestMappingContext(String name, String pathRegex,
                                 String targetPath,
                                 RoutingContext routingContext) {
        this.name = name;
        this.pathRegex = compile(pathRegex);
        this.rewritePath = new PathTemplate(targetPath);
        this.routingContext = routingContext;
    }

    public String getName() {
        return name;
    }

    public Pattern getPathRegex() {
        return pathRegex;
    }

    public RoutingContext getRoutingContext() {
        return routingContext;
    }

    public PathTemplate getRewritePath() {
        return rewritePath;
    }

    @Override
    public String toString() {
        return name + ":" + pathRegex.pattern();
    }
}
