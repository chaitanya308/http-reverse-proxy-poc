package com.poc.proxy.interceptors;

import com.poc.proxy.interceptors.ProxyHttpRequestInterceptor;
import io.micrometer.core.instrument.MeterRegistry;

public abstract class AbstractMetricInterceptor extends ProxyHttpRequestInterceptor {

    protected final MeterRegistry meterRegistry;

    public AbstractMetricInterceptor(MeterRegistry meterRegistry) {
        this.meterRegistry = meterRegistry;
    }
}
