package com.tms.profilemanager.app.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;
import org.zalando.logbook.BodyFilter;
import org.zalando.logbook.BodyFilters;
import org.zalando.logbook.Correlation;
import org.zalando.logbook.DefaultHttpLogWriter;
import org.zalando.logbook.DefaultSink;
import org.zalando.logbook.HttpRequest;
import org.zalando.logbook.HttpResponse;
import org.zalando.logbook.Logbook;
import org.zalando.logbook.Precorrelation;
import org.zalando.logbook.Sink;
import org.zalando.logbook.SplunkHttpLogFormatter;
import org.zalando.logbook.Strategy;
import org.zalando.logbook.json.JsonBodyFilters;
import org.zalando.logbook.servlet.LogbookFilter;

import java.io.IOException;
import java.util.Set;
import java.util.stream.Stream;

import static org.zalando.logbook.Conditions.requestTo;

@Configuration
@ConditionalOnProperty(name = "logbook.enabled", havingValue = "true", matchIfMissing = true)
public class LogbookConfiguration {

    @Value("${logbook.log-request-body}")
    private boolean logRequestBody;
    @Value("${logbook.log-successful-requests}")
    private boolean logSuccessfulRequests;

    @Bean
    public Logbook logbook() {
        return Logbook.builder()
                .condition(requestTo("/api/**"))
                .bodyFilter(defaultBodyFilter())
                .sink(new DefaultSink(new SplunkHttpLogFormatter(), new DefaultHttpLogWriter()))
                .build();
    }

    @Bean
    public Logbook logbookExternalClient() {
        return Logbook.builder()
                .condition(requestTo("/**"))
                .bodyFilter(defaultBodyFilter())
                .sink(new DefaultSink(new SplunkHttpLogFormatter(), new DefaultHttpLogWriter()))
                .build();
    }

    @Bean
    public LogbookFilter logbookFilter() {
        return new LogbookFilter(logbook(), logbookStrategy());
    }

    public Strategy logbookStrategy() {
        return new Strategy() {

            @Override
            public void write(Precorrelation precorrelation, HttpRequest request, Sink sink) {
                // defer decision until response is available
            }

            @Override
            public void write(Correlation correlation, HttpRequest request, HttpResponse response, Sink sink)
                    throws IOException {
                HttpStatus status = HttpStatus.valueOf(response.getStatus());
                if (status.is4xxClientError() || status.is5xxServerError()) {
                    if (logRequestBody) {
                        sink.writeBoth(correlation, request, response);
                    } else {
                        sink.writeBoth(correlation, request.withoutBody(), response);
                    }
                } else if (logSuccessfulRequests) {
                    if (logRequestBody) {
                        sink.writeBoth(correlation, request, response.withoutBody());
                    } else {
                        sink.writeBoth(correlation, request.withoutBody(), response.withoutBody());
                    }
                }
            }
        };
    }

    private BodyFilter defaultBodyFilter() {
        return Stream.of(
                        BodyFilters.defaultValue(),
                        JsonBodyFilters.replaceJsonStringProperty(Set.of(
                                "password",
                                "accessToken",
                                "refreshToken"
                        ), "***")
                )
                .reduce(BodyFilter::merge)
                .orElse(BodyFilters.defaultValue());
    }
}
