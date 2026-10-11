package com.tms.profilemanager.app.config;

import com.tms.profilemanager.app.utils.ClockUtils;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.Clock;
import java.time.ZoneId;

@Configuration
public class ClockConfiguration {

    @Bean
    public Clock clock() {
        return Clock.tickMillis(ZoneId.systemDefault());
    }

    @Bean
    protected ClockUtilsInitializer clockUtilsInitializer(Clock clock) {
        return new ClockUtilsInitializer(clock);
    }

    protected static class ClockUtilsInitializer {

        public ClockUtilsInitializer(Clock clock) {
            ClockUtils.setClock(clock);
        }
    }
}
