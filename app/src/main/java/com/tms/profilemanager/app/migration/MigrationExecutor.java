package com.tms.profilemanager.app.migration;

import com.tms.profilemanager.app.entity.MigrationExecution;
import com.tms.profilemanager.app.repository.MigrationExecutionRepository;
import com.tms.profilemanager.app.utils.ClockUtils;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.event.ApplicationStartedEvent;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.event.EventListener;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.util.Comparator;
import java.util.List;

import static java.util.stream.Collectors.joining;
import static org.apache.commons.collections4.CollectionUtils.isNotEmpty;

@Slf4j
@Component
@FieldDefaults(level = AccessLevel.PRIVATE)
public class MigrationExecutor {

    final List<Migration> migrations;
    final MigrationExecutionRepository migrationExecutionRepository;
    final ConfigurableApplicationContext applicationContext;

    @Getter
    boolean migrationsCompleted;

    public MigrationExecutor(List<Migration> migrations,
                             MigrationExecutionRepository migrationExecutionRepository,
                             ConfigurableApplicationContext applicationContext) {
        this.migrations = migrations;
        this.migrations.sort(Comparator.comparing(it -> it.getClass().getSimpleName()));
        this.migrationExecutionRepository = migrationExecutionRepository;
        this.applicationContext = applicationContext;
    }

    @EventListener(ApplicationStartedEvent.class)
    @Order(Ordered.HIGHEST_PRECEDENCE)
    public void executeMigrations() {
        migrations.forEach(this::execute);
        migrationsCompleted = true;
        log.info("All migrations applied");
    }

    private void execute(Migration migration) {
        String migrationName = getMigrationName(migration);
        String migrationVersion = migrationName.substring(0, migrationName.lastIndexOf("_"));
        try {
            if (migrationExecutionRepository.findByName(migrationName).isEmpty()) {
                List<MigrationExecution> conflictMigrations =
                        migrationExecutionRepository.findMigrationsWithHigherVersionsThan(migrationName, migrationVersion);
                if (isNotEmpty(conflictMigrations)) {
                    throw new IllegalStateException("Applied migration with greater or equal version found: "
                            + conflictMigrations.stream().map(MigrationExecution::getName).collect(joining(", ")));
                }

                log.info("{} started", migrationName);
                migration.execute();
                log.info("{} finished", migrationName);

                MigrationExecution migrationExecution = new MigrationExecution();
                migrationExecution.setExecutionTime(ClockUtils.now());
                migrationExecution.setName(migrationName);
                migrationExecution.setVersion(migrationVersion);
                migrationExecutionRepository.save(migrationExecution);
            }
        } catch (Exception e) {
            log.error("Error during \"{}\" migration execution", migrationName, e);
            applicationContext.close();
        }
    }

    private String getMigrationName(Migration migration) {
        String className = migration.getClass().getSimpleName();
        int index = className.indexOf("$");
        return index != -1 ? className.substring(0, index) : className;
    }
}
