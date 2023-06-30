package com.zelusik.eatery.config;

import com.zelusik.eatery.security.*;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Import;

@Import({
        SecurityConfig.class,
        JwtAccessDeniedHandler.class,
        JwtAuthenticationEntryPoint.class,
        JwtExceptionFilter.class,
        JwtAuthenticationFilter.class,
        JwtTokenProvider.class
})
@TestConfiguration
public class TestSecurityConfig {
}
