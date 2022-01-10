package com.poc.proxy.interceptors;

import io.micrometer.core.instrument.MeterRegistry;
import org.apache.http.client.utils.URIBuilder;
import org.slf4j.Logger;
import org.springframework.http.HttpRequest;
import org.springframework.http.client.ClientHttpRequestExecution;
import org.springframework.http.client.ClientHttpResponse;

import java.io.IOException;

import static org.slf4j.LoggerFactory.getLogger;

public class StatusCodeMetricInterceptor extends AbstractMetricInterceptor {

    private static final Logger LOGGER = getLogger(StatusCodeMetricInterceptor.class);

    public StatusCodeMetricInterceptor(MeterRegistry meterRegistry) {
        super(meterRegistry);
    }

    @Override
    public ClientHttpResponse intercept(HttpRequest request,
                                        byte[] body,
                                        ClientHttpRequestExecution execution) throws IOException {

        LOGGER.debug("Inside status code metric interceptor, for request {}" + request.getURI());

        String requestUri = new URIBuilder(request.getURI()).getPath();

        try {
            // Log metric for status code by request
            ClientHttpResponse response = execution.execute(request, body);
            String metricName = requestUri + ".response." + response.getStatusCode().value();
            this.meterRegistry.counter(metricName).increment();
            return response;

        } catch (Exception ex) {
            // Log exception metric for the response by request uri
            String metricName = requestUri + ".response.exception";
            this.meterRegistry.counter(metricName).increment();
            throw ex;
        }
    }
}
