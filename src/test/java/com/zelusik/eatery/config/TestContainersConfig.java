package com.zelusik.eatery.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.utility.DockerImageName;

@Profile("test")
@Configuration
public class TestContainersConfig {

    private static final String REDIS_DOCKER_IMAGE = "redis:7.0.8-alpine";
    private static final int REDIS_PORT = 6379;

    static {
        try (GenericContainer<?> redisContainer = new GenericContainer<>(DockerImageName.parse(REDIS_DOCKER_IMAGE))) {
            redisContainer.withExposedPorts(REDIS_PORT).withReuse(true);
            redisContainer.start();
            System.setProperty("spring.redis.host", redisContainer.getHost());
            System.setProperty("spring.redis.port", redisContainer.getMappedPort(REDIS_PORT).toString());
        }
    }
}
