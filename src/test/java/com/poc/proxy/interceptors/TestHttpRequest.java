package com.poc.proxy.interceptors;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpRequest;

import java.net.URI;

public class TestHttpRequest implements HttpRequest {

    private URI uri;

    public TestHttpRequest(URI uri) {
        this.uri = uri;
    }

    @Override
    public String getMethodValue() {
        return null;
    }

    @Override
    public URI getURI() {
        return uri;
    }

    @Override
    public HttpHeaders getHeaders() {
        return null;
    }
}