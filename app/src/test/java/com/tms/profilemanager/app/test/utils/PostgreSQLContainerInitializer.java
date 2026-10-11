package com.tms.profilemanager.app.test.utils;

import org.springframework.boot.test.util.TestPropertyValues;
import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.ConfigurableApplicationContext;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.utility.DockerImageName;


public class PostgreSQLContainerInitializer implements ApplicationContextInitializer<ConfigurableApplicationContext> {

    private static final PostgreSQLContainer<?> dbContainer;

    static {
        final DockerImageName postgresImage = DockerImageName.parse("postgres:11-alpine")
                .asCompatibleSubstituteFor("postgres");

        dbContainer = new PostgreSQLContainer<>(postgresImage)
                .withDatabaseName("profile_manager")
                .withUsername("test")
                .withPassword("test")
                .withReuse(true);
        dbContainer.start();
    }

    @Override
    public void initialize(final ConfigurableApplicationContext configurableApplicationContext) {
        TestPropertyValues.of(
                "spring.datasource.url=" + dbContainer.getJdbcUrl(),
                "spring.datasource.username=" + dbContainer.getUsername(),
                "spring.datasource.password=" + dbContainer.getPassword()
        ).applyTo(configurableApplicationContext.getEnvironment());
    }
}
