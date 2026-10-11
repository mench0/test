package com.tms.profilemanager.cache.test.utils;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.utility.DockerImageName;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class RedisContainerUtils {

    public static GenericContainer createContainerAndStart() {
        final DockerImageName image = DockerImageName.parse("redis:7.2-rc-alpine")
                .asCompatibleSubstituteFor("redis");

        var redisContainer = new GenericContainer<>(image)
                .withExposedPorts(6379);
        redisContainer.start();
        return redisContainer;
    }
}
