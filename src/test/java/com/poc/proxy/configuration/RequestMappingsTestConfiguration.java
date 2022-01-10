package com.poc.proxy.configuration;

import com.poc.proxy.forward.RequestMappingContext;
import com.poc.proxy.forward.RequestMappingResolver;
import com.poc.proxy.forward.RoutingContext;
import com.poc.proxy.interceptors.TargetHostRoutingInterceptor;
import com.poc.proxy.interceptors.TargetPathRewriteInterceptor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Arrays;
import java.util.List;

@Configuration
public class RequestMappingsTestConfiguration {
    @Bean
    public List<RequestMappingContext> requestMappingsList() {
        return Arrays.asList(
                new RequestMappingContext("test-mapping1",
                        "/test1", "/rewrite1/path2",
                        new RoutingContext("www.abc.com")),
                new RequestMappingContext("test-mapping3",
                        "/test3", "/test3",
                        new RoutingContext("test-host")));
    }

    @Bean
    public RequestMappingResolver requestMappingResolver(List<RequestMappingContext> requestMappingsList) {
        return new RequestMappingResolver(requestMappingsList);
    }

    @Bean
    public TargetPathRewriteInterceptor targetPathRewriteInterceptor(RequestMappingResolver requestMappingResolver) {
        return new TargetPathRewriteInterceptor(requestMappingResolver);
    }

    @Bean
    public TargetHostRoutingInterceptor targetHostRoutingInterceptor(RequestMappingResolver requestMappingResolver) {
        return new TargetHostRoutingInterceptor(requestMappingResolver);
    }
}
