package com.poc.proxy.interceptors;

import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Timer;
import org.apache.http.client.utils.URIBuilder;
import org.slf4j.Logger;
import org.springframework.http.HttpRequest;
import org.springframework.http.client.ClientHttpRequestExecution;
import org.springframework.http.client.ClientHttpResponse;

import java.io.IOException;
import java.time.Duration;

import static java.lang.System.nanoTime;
import static org.slf4j.LoggerFactory.getLogger;

public class LatencyMetricInterceptor extends AbstractMetricInterceptor {

    private static final Logger LOGGER = getLogger(LatencyMetricInterceptor.class);

    private static final double[] PERCENTILE_ARR = new double[] {0.25, 0.50, 0.75, 0.90, 0.99};

    public LatencyMetricInterceptor(MeterRegistry meterRegistry) {
        super(meterRegistry);
    }

    @Override
    public ClientHttpResponse intercept(HttpRequest request,
                                        byte[] body,
                                        ClientHttpRequestExecution execution) throws IOException {

        LOGGER.debug("Inside latency metric interceptor, for request {}" + request.getURI());

        long startingTime = nanoTime();
        String requestUri = new URIBuilder(request.getURI()).getPath();
        try {
            return execution.execute(request, body);
        } finally {
            Duration responseTime = Duration.ofNanos(nanoTime() - startingTime);
            Timer.builder(requestUri)
                    .publishPercentiles(PERCENTILE_ARR)
                    .publishPercentileHistogram()
                    .register(this.meterRegistry)
                    .record(responseTime);
        }
    }
}
