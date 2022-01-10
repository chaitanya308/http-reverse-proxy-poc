package com.poc.proxy.configuration;

import com.poc.proxy.forward.RequestMappingContext;
import com.poc.proxy.forward.RequestMappingResolver;
import com.poc.proxy.forward.RoutingContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Arrays;
import java.util.List;

@Configuration
public class RequestMappingsConfiguration {

    @Bean
    public List<RequestMappingContext> requestMappingsList() {
        // TODO - the routes should be specified through a config/properties file, and
        // the mappings should be created by reading the routes files.
        return Arrays.asList(
                new RequestMappingContext("healthcheck",
                        "/healthcheck", "/healthcheck",
                        new RoutingContext("api.netflix.com")),
                new RequestMappingContext("account-geo",
                        "/account/geo", "/account/geo",
                        new RoutingContext("api.netflix.com")),
                new RequestMappingContext("test",
                        "/test", "/test",
                        new RoutingContext("api.netflix.com")),
                new RequestMappingContext("root", "/", "/",
                        new RoutingContext("www.netflix.com")));
    }

    @Bean
    public RequestMappingResolver requestMappingResolver(List<RequestMappingContext> requestMappingsList) {
        return new RequestMappingResolver(requestMappingsList);
    }
}
