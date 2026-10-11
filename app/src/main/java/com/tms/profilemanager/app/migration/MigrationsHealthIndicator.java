package com.tms.profilemanager.app.migration;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.HealthIndicator;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class MigrationsHealthIndicator implements HealthIndicator {

    MigrationExecutor migrationExecutor;

    @Override
    public Health health() {
        if (migrationExecutor.isMigrationsCompleted()) {
            return Health.up().build();
        }
        return Health.down().build();
    }
}