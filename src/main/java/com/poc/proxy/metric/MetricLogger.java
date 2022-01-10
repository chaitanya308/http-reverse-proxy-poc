package com.poc.proxy.metric;

import io.micrometer.core.instrument.Meter;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Timer;
import io.micrometer.core.instrument.distribution.ValueAtPercentile;
import org.slf4j.Logger;
import org.springframework.scheduling.annotation.Scheduled;

import java.util.concurrent.TimeUnit;

import static org.slf4j.LoggerFactory.getLogger;

public class MetricLogger {

    private static final int LOGGING_INTERVAL_IN_MILLIS = 60 * 1000;
    private static final Logger LOGGER = getLogger(MetricLogger.class);

    private final MeterRegistry meterRegistry;

    public MetricLogger(MeterRegistry meterRegistry) {
        this.meterRegistry = meterRegistry;
    }

    @Scheduled(fixedDelay = LOGGING_INTERVAL_IN_MILLIS) // Log every minute
    public void printMetricsToLogFile() {

        // Log status code metrics
        LOGGER.info("========Status Codes (cumulative counts)=========");

        // Print all the meters of COUNTER type i.e status code counters
        for (Meter meter : this.meterRegistry.getMeters()) {
            String metricName = meter.getId().getName();

            if (meter.getId().getType() == Meter.Type.COUNTER) {
                LOGGER.info(metricName + " - " + (int)this.meterRegistry.counter(metricName).count());
            }
        }

        LOGGER.info("========Response Times (in the last minute)=========");

        // Print all the meters of TIMER type i.e latency timers
        for (Meter meter : this.meterRegistry.getMeters()) {
            String metricName = meter.getId().getName();

            if (meter.getId().getType() == Meter.Type.TIMER) {
                Timer timer = this.meterRegistry.timer(metricName);
                ValueAtPercentile[] percentiles = timer.takeSnapshot().percentileValues();

                // Iterate through
                for (ValueAtPercentile percentile : percentiles) {
                    LOGGER.info("{} {}th percentile response time is {}ms",
                            metricName,
                            (int)(percentile.percentile() * 100),
                            percentile.value(TimeUnit.MILLISECONDS));
                }
            }
        }
    }
}
