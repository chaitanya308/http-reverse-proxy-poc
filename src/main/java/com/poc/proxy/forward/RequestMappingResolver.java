package com.poc.proxy.forward;

import com.poc.proxy.exception.MappingNotFoundException;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.CollectionUtils;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

import static java.util.stream.Collectors.joining;
import static java.util.stream.Collectors.toList;
import static org.slf4j.LoggerFactory.getLogger;

/**
 * - Maintains a list of mappings configured on the proxy, and
 * - resolves to a mapping for an incoming request.
 */
public class RequestMappingResolver {

    private static final Logger LOGGER = getLogger(RequestMappingResolver.class);

    private final List<RequestMappingContext> requestMappingContextList;

    @Autowired
    public RequestMappingResolver(List<RequestMappingContext> requestMappingContextList) {
        this.requestMappingContextList = requestMappingContextList;
    }

    public RequestMappingContext resolveRequestToMapping(String requestURI) {

        // Filter through the configured mappings, and find the matching one for the request
        List<RequestMappingContext> contextList = requestMappingContextList.stream()
                .filter(context -> context.getPathRegex().matcher(requestURI).matches())
                .collect(toList());

        if (CollectionUtils.isEmpty(contextList)) {
            LOGGER.error("No request mapping matches {} incoming request", requestURI);
            throw new MappingNotFoundException("No matching mapping found for request: " + requestURI);
        }

        if (contextList.size() > 1) {
            String mappings = contextList.stream().map(RequestMappingContext::toString).collect(joining(","));
            LOGGER.error("Incoming request {} is resolved to multiple mappings: {}.", requestURI, mappings);
            throw new MappingNotFoundException("Multiple mappings " + mappings + " found for request" + requestURI);
        }

        RequestMappingContext context = contextList.get(0);
        return context;
    }

    public List<RequestMappingContext> getRequestMappingContextList() {
        return requestMappingContextList;
    }
}
