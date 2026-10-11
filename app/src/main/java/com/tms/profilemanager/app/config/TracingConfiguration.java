package com.tms.profilemanager.app.config;

import brave.handler.MutableSpan;
import brave.handler.SpanHandler;
import brave.propagation.TraceContext;
import io.micrometer.core.instrument.util.StringUtils;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class TracingConfiguration {

    @Bean
    public SpanHandler spanHandler() {
        return new SpanHandler() {

            @Override
            public boolean end(TraceContext traceContext, MutableSpan span, Cause cause) {
                if (span.name().equals("select")) {
                    String jdbcQuery = span.tags().get("jdbc.query");
                    if (jdbcQuery != null) {
                        jdbcQuery = jdbcQuery.toLowerCase().replaceFirst("(?<=select )(.+)(?= from)", "...");
                        span.name(StringUtils.truncate(jdbcQuery, 100, " ..."));
                    }
                }
                return true; // keep this span
            }
        };
    }
}
