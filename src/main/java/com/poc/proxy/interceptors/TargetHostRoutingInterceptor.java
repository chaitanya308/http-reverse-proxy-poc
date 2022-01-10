package com.poc.proxy.interceptors;

import com.poc.proxy.forward.RequestMappingContext;
import com.poc.proxy.forward.RequestMappingResolver;
import org.apache.http.client.utils.URIBuilder;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpRequest;
import org.springframework.http.client.ClientHttpRequestExecution;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.http.client.support.HttpRequestWrapper;
import org.springframework.util.Assert;

import java.io.IOException;
import java.net.URI;

import static org.slf4j.LoggerFactory.getLogger;
import static org.springframework.web.util.UriComponentsBuilder.fromUri;

public class TargetHostRoutingInterceptor extends ProxyHttpRequestInterceptor {

    private static final Logger LOGGER = getLogger(TargetHostRoutingInterceptor.class);

    private final RequestMappingResolver requestMappingResolver;

    @Autowired
    public TargetHostRoutingInterceptor(RequestMappingResolver requestMappingResolver) {
        this.requestMappingResolver = requestMappingResolver;
    }

    @Override
    public ClientHttpResponse intercept(HttpRequest request, byte[] body,
                                        ClientHttpRequestExecution execution) throws IOException {
        // Find the mapping context for this request
        String requestUri = new URIBuilder(request.getURI()).getPath();
        RequestMappingContext mappingContext = requestMappingResolver.resolveRequestToMapping(requestUri);
        Assert.notNull(mappingContext, "Request mapping context cannot be null");

        // create the target host (origin) URI
        // TODO - Load-balancer should be employed here while figuring the route host.
        URI routeHostUri = mappingContext.getRoutingContext().getRoutingUri();
        URI rewrittenUri = fromUri(request.getURI())
                .scheme(routeHostUri.getScheme())
                .host(routeHostUri.getHost())
                .port(routeHostUri.getPort())
                .build(true)
                .toUri();

        // Update the the request with the URI
        HttpRequest updatedRequest = new HttpRequestWrapper(request) {
            @Override
            public URI getURI() {
                return rewrittenUri;
            }
        };

        LOGGER.info("Rewrote url {} to {} ", request.getURI(), updatedRequest.getURI());
        ClientHttpResponse response = execution.execute(updatedRequest, body);
        return response;
    }
}
