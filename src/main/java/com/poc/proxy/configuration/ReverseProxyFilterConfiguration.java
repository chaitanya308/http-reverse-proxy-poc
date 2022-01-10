package com.poc.proxy.configuration;

import com.poc.proxy.filter.ReverseProxyFilter;
import com.poc.proxy.forward.RequestMappingResolver;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.web.client.RestTemplate;

@Import({RequestMappingsConfiguration.class,
        RestTemplateAndInterceptorsConfiguration.class})
@Configuration
public class ReverseProxyFilterConfiguration {

    @Bean
    public ReverseProxyFilter reverseProxyFilter(RequestMappingResolver requestMappingResolver,
                                                 RestTemplate restTemplate) {
        return new ReverseProxyFilter(requestMappingResolver, restTemplate);
    }
}
