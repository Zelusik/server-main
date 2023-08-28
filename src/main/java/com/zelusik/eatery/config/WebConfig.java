package com.zelusik.eatery.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Value("${eatery.dev.url}")
    private String eateryDevUrl;

    @Value("${eatery.prod.url}")
    private String eateryProdUrl;

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**")
                .allowedOrigins(
                        "http://localhost", "http://localhost:8080",
                        eateryDevUrl, eateryDevUrl + ":8080",
                        eateryProdUrl, eateryProdUrl + ":8080"
                )
                .allowedMethods("*");
    }
}