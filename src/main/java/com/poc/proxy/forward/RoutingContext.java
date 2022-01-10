package com.poc.proxy.forward;

import java.net.URI;

/**
 * This class represents the minimum config needed for forwarding the request in this exercise.
 * TODO - this has to be enhanced to incorporate load-balancing while picking the target server name.
 */
public class RoutingContext {
    private static int DEFAULT_PORT = 80;

    private String targetServerName;
    private int port = DEFAULT_PORT;

    public RoutingContext(String serverName) {
        this.targetServerName = serverName;
    }

    public RoutingContext(String serverName, int port) {
        this.targetServerName = serverName;
        this.port = port;
    }

    public String getTargetServerName() {
        return targetServerName;
    }

    public int getPort() {
        return port;
    }

    public URI getRoutingUri() {
        return URI.create("http://" + this.getTargetServerName());
    }
}
