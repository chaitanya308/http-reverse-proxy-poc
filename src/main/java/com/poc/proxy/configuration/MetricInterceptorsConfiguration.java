package com.poc.proxy.configuration;

import com.poc.proxy.interceptors.LatencyMetricInterceptor;
import com.poc.proxy.interceptors.StatusCodeMetricInterceptor;
import com.poc.proxy.metric.MetricLogger;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.simple.SimpleMeterRegistry;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;

@Configuration
@EnableScheduling
public class MetricInterceptorsConfiguration {

    @Bean
    public MeterRegistry meterRegistry() {
        return new SimpleMeterRegistry();
    }

    @Bean
    public StatusCodeMetricInterceptor statusCodeMetricInterceptor(MeterRegistry meterRegistry) {
        return new StatusCodeMetricInterceptor(meterRegistry);
    }

    @Bean
    public LatencyMetricInterceptor latencyMetricInterceptor(MeterRegistry meterRegistry) {
        return new LatencyMetricInterceptor(meterRegistry);
    }

    @Bean
    public MetricLogger metricLogger(MeterRegistry meterRegistry) {
        return new MetricLogger(meterRegistry);
    }
}
