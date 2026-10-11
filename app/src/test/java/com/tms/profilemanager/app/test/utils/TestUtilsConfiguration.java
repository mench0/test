package com.tms.profilemanager.app.test.utils;

import com.tms.profilemanager.app.utils.ClockUtils;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

import java.time.Clock;
import java.time.Instant;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.util.TimeZone;

@Configuration
@ComponentScan
public class TestUtilsConfiguration {

    static {
        TimeZone.setDefault(TimeZone.getTimeZone("UTC"));
        ClockUtils.setClock(createTestClock());
    }

    @Bean
    @Primary
    public Clock testClock() {
        return createTestClock();
    }

    private static Clock createTestClock() {
        Clock clock = Clock.tickMillis(ZoneOffset.UTC);
        return new Clock() {
            @Override
            public ZoneId getZone() {
                return clock.getZone();
            }

            @Override
            public Clock withZone(ZoneId zone) {
                return clock.withZone(zone);
            }

            @Override
            public Instant instant() {
                return clock.instant();
            }
        };
    }
}
