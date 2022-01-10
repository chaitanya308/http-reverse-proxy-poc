package com.poc.proxy.filter;

import com.poc.proxy.exception.MappingNotFoundException;
import com.poc.proxy.forward.RequestMappingResolver;
import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.Ordered;
import org.springframework.http.*;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpServletResponseWrapper;
import java.io.IOException;
import java.net.URI;
import java.time.Duration;
import java.util.Enumeration;
import java.util.List;

import static java.net.URI.create;
import static java.util.Collections.list;
import static org.slf4j.LoggerFactory.getLogger;

/**
 * This filter intercepts the requests to this server by being highest in order,
 * and forwards the requests to origin servers and returns the response to the
 * client, without continuing with further filter chain execution.
 */
public class ReverseProxyFilter extends OncePerRequestFilter implements Ordered {

    private static final Logger LOGGER = getLogger(ReverseProxyFilter.class);
    private static final String X_EXECUTION_TIME_HEADER = "X-execution.time";

    private final RequestMappingResolver requestMappingResolver;

    /*
     * TODO - instead of having global restTemplate that serves all the requests, have
     * a template per RequestMappingContext, that way timeouts based on URL can be configured.
     *
     * RestTemplate is already configured with list of interceptors like URL rewriter, metric capturing etc.
     */
    private final RestTemplate restTemplate;

    @Autowired
    public ReverseProxyFilter(RequestMappingResolver requestMappingResolver,
                              RestTemplate restTemplate) {
        this.requestMappingResolver = requestMappingResolver;
        this.restTemplate = restTemplate;
    }

    @Override
    public int getOrder() {
        return Ordered.HIGHEST_PRECEDENCE;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws IOException {
        try {
            // Verify that request uri has a valid mapping to forward the request
            requestMappingResolver.resolveRequestToMapping(request.getRequestURI());
        } catch (MappingNotFoundException e) {
            // If no mapping found, or multiple mappings are found, return an error
            handleException(e, response, HttpStatus.NOT_FOUND);
            return;
        }

        long startingTime = System.currentTimeMillis();

        // Forward the request to the target server (origin)
        RequestEntity<byte[]> requestEntity = prepareRequestEntity(request);
        try {
            ResponseEntity<byte[]> responseEntity = restTemplate.exchange(requestEntity, byte[].class);
            long executionTime = System.currentTimeMillis() - startingTime;
            updateHttpServletResponse(responseEntity, response, executionTime);

        } catch (RestClientException e) {
            LOGGER.error("Error forwarding the request", e);
            handleException(e, response, HttpStatus.INTERNAL_SERVER_ERROR);

        }
        LOGGER.debug("{} returned status {}", request.getRequestURI(), response.getStatus());
    }

    private void handleException(Exception e, HttpServletResponse response, HttpStatus status) throws IOException {
        LOGGER.error(e.getMessage(), e);
        response.setStatus(status.value());
        // TODO - exception messages are internal and should not be returned to the client as is, as they might expose
        // sensitive information. Instead each exception should have an internal code, and there should be a translation from
        // internal code to external http code, external error message (which is safe and acceptable)
        // This mapping can be defined through internal/external configuration/properties, and translation functionality
        // will exist as separate module, instead of this private method :)
        response.getWriter().write(e.getMessage());
    }

    RequestEntity<byte[]> prepareRequestEntity(HttpServletRequest request) throws IOException {
        byte[] body = IOUtils.toByteArray(request.getInputStream());
        HttpHeaders headers = extractHeaders(request);
        HttpMethod method = HttpMethod.resolve(request.getMethod());
        URI uri = extractUri(request);
        return new RequestEntity<>(body, headers, method, uri);
    }

    private URI extractUri(HttpServletRequest request) {
        String query = request.getQueryString() == null ? "" : "?" + request.getQueryString();
        return create(request.getRequestURL().append(query).toString());
    }

    private HttpHeaders extractHeaders(HttpServletRequest request) {
        HttpHeaders headers = new HttpHeaders();
        Enumeration<String> headerNames = request.getHeaderNames();
        while (headerNames.hasMoreElements()) {
            String name = headerNames.nextElement();
            List<String> value = list(request.getHeaders(name));
            headers.put(name, value);
        }
        return headers;
    }

    void updateHttpServletResponse(ResponseEntity<byte[]> responseEntity,
                                   HttpServletResponse response,
                                   long executionTime) throws IOException {
        response.setStatus(responseEntity.getStatusCodeValue());

        // add response headers
        responseEntity.getHeaders().forEach((name, values) ->
                values.forEach(value -> response.addHeader(name, value)));

        response.addHeader(X_EXECUTION_TIME_HEADER, String.valueOf(executionTime));

        // Write the response body
        if (responseEntity.getBody() != null) {
            response.getOutputStream().write(responseEntity.getBody());
        }
    }
}
