package com.zelusik.eatery.global.config;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;

@Configuration
public class RedisConfig {

    private final String host;
    private final int port;

    public RedisConfig(
            @Value("${spring.redis.host}") String host,
            @Value("${spring.redis.port}") int port
    ) {
        this.host = host;
        this.port = port;
    }

    @Primary
    @Bean
    public RedisConnectionFactory defaultRedisConnectionFactory() {
        return createLettuceConnectionFactory(0);
    }

    @Qualifier("redisConnectionFactoryForCaching")
    @Bean
    public RedisConnectionFactory redisConnectionFactoryForCaching() {
        return createLettuceConnectionFactory(1);
    }

    private RedisConnectionFactory createLettuceConnectionFactory(int dbIndex) {
        RedisStandaloneConfiguration redisStandaloneConfig = new RedisStandaloneConfiguration();
        redisStandaloneConfig.setHostName(host);
        redisStandaloneConfig.setPort(port);
        redisStandaloneConfig.setDatabase(dbIndex);
        return new LettuceConnectionFactory(redisStandaloneConfig);
    }
}
