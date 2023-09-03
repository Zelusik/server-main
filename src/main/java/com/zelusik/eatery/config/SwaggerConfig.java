package com.zelusik.eatery.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.ExternalDocumentation;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springdoc.core.GroupedOpenApi;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SwaggerConfig {

    @Bean
    public OpenAPI eateryApi(@Value("${eatery.app.version}") String appVersion) {
        return new OpenAPI()
                .info(new Info()
                        .title("Eatery API Docs")
                        .description("Eatery의 API 명세서")
                        .version(appVersion))
                .externalDocs(new ExternalDocumentation()
                        .description("Github organization of team eatery")
                        .url("https://github.com/Zelusik"))
                .components(new Components().addSecuritySchemes(
                        "access-token",
                        new SecurityScheme().type(SecurityScheme.Type.HTTP).scheme("Bearer").bearerFormat("JWT")
                ));
    }

    @Bean
    public GroupedOpenApi groupedOpenApiVersion1() {
        return GroupedOpenApi.builder()
                .group("v1")
                .packagesToScan("com.zelusik.eatery.controller")
                .pathsToMatch("/api/v1/**")
                .build();
    }
}
