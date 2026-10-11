package com.tms.profilemanager.app.test.utils;

import org.springframework.boot.test.util.TestPropertyValues;
import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.ConfigurableApplicationContext;
import org.testcontainers.containers.GenericContainer;


public class RedisContainerInitializer implements ApplicationContextInitializer<ConfigurableApplicationContext> {

    GenericContainer<?> redisContainer = RedisContainerUtils.createContainerAndStart();

    @Override
    public void initialize(final ConfigurableApplicationContext configurableApplicationContext) {
        TestPropertyValues.of(
                "spring.redis.host=" + redisContainer.getHost(),
                "spring.redis.port=" + redisContainer.getMappedPort(6379).toString()
        ).applyTo(configurableApplicationContext.getEnvironment());

        // rely on ryuk container because stopping containers from java code can work too slow
        // configurableApplicationContext.addApplicationListener((ContextClosedEvent event) -> redisContainer.stop());
    }
}
