package com.poc.proxy.interceptors;

import com.poc.proxy.forward.RequestMappingContext;
import com.poc.proxy.forward.RequestMappingResolver;
import org.slf4j.Logger;
import org.springframework.http.HttpRequest;
import org.springframework.http.client.ClientHttpRequestExecution;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.http.client.support.HttpRequestWrapper;
import org.springframework.util.Assert;

import java.io.IOException;
import java.net.URI;
import java.util.regex.Matcher;

import static org.slf4j.LoggerFactory.getLogger;
import static org.springframework.web.util.UriComponentsBuilder.fromUri;

public class TargetPathRewriteInterceptor extends ProxyHttpRequestInterceptor {

    private static final Logger LOGGER = getLogger(TargetPathRewriteInterceptor.class);

    private final RequestMappingResolver requestMappingResolver;

    public TargetPathRewriteInterceptor(RequestMappingResolver requestMappingResolver) {
        this.requestMappingResolver = requestMappingResolver;
    }

    @Override
    public ClientHttpResponse intercept(HttpRequest request,
                                        byte[] body,
                                        ClientHttpRequestExecution execution) throws IOException {

        // Find the mapping context for this request
        String requestUri = request.getURI().getRawPath();
        RequestMappingContext mappingContext = requestMappingResolver.resolveRequestToMapping(requestUri);
        Assert.notNull(mappingContext, "Request mapping context cannot be null");

        Matcher matcher = mappingContext.getPathRegex().matcher(requestUri);

        String rewrittenPath;
        try {
            rewrittenPath = mappingContext.getRewritePath().fill(matcher);
        } catch (IllegalArgumentException e) {
            throw new RuntimeException("Failed to rewrite URI path", e);
        }

        URI rewrittenUri = fromUri(request.getURI())
                .replacePath(rewrittenPath)
                .build(true)
                .toUri();

        // Update the the request with the URI

        // TODO - Ideally it is better to introduce my own classes that implement HttpRequest and ClientHttpResponse
        // which I can then easily modify and pass them around, instead of using wrapper class like below.
        // Having my own classes will also help in enclosing request, response manipulation code. And it would also
        // help in developing unit tests easily, where we have to manipulate requests and responses in test methods.

        HttpRequest updatedRequest = new HttpRequestWrapper(request) {
            @Override
            public URI getURI() {
                return rewrittenUri;
            }
        };

        LOGGER.info("Rewrote path in {} to {} ", request.getURI(), updatedRequest.getURI());
        ClientHttpResponse response = execution.execute(updatedRequest, body);
        return response;
    }
}
