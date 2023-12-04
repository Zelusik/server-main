package com.zelusik.eatery.global.config;

import com.zelusik.eatery.domain.member.constant.RoleType;
import com.zelusik.eatery.global.auth.JwtAccessDeniedHandler;
import com.zelusik.eatery.global.auth.JwtAuthenticationEntryPoint;
import com.zelusik.eatery.global.auth.JwtAuthenticationFilter;
import com.zelusik.eatery.global.auth.JwtExceptionFilter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.security.servlet.PathRequest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.CorsUtils;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

import static org.springframework.http.HttpMethod.*;
import static org.springframework.http.HttpMethod.OPTIONS;

@Slf4j
@RequiredArgsConstructor
@Configuration
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtAuthenticationFilter;
    private final JwtExceptionFilter jwtExceptionFilter;
    private final JwtAccessDeniedHandler jwtAccessDeniedHandler;
    private final JwtAuthenticationEntryPoint jwtAuthenticationEntryPoint;

    private static final String[] AUTH_WHITE_PATHS = {
            "/api/v*/auth/login/**",
            "/api/v*/auth/token",
            "/api/v*/auth/validity"
    };

    private static final Map<String, HttpMethod> ADMIN_AUTH_LIST = Map.of(
            "/api/v*/places/*/menus", HttpMethod.DELETE
    );

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        String[] rolesAboveAdmin = {RoleType.ADMIN.name()};
        return http
                .httpBasic().disable()
                .csrf().disable()
                .formLogin().disable()
                .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS).and()
                .cors().and()
                .authorizeHttpRequests(auth -> {
                    auth
                            .requestMatchers(PathRequest.toStaticResources().atCommonLocations()).permitAll()
                            .requestMatchers(CorsUtils::isPreFlightRequest).permitAll()
                            .mvcMatchers("/swagger-ui/**", "/v3/api-docs/**", "/actuator/**").permitAll();
                    Arrays.stream(AUTH_WHITE_PATHS).forEach(authWhiteListElem -> auth.mvcMatchers(authWhiteListElem).permitAll());
                    ADMIN_AUTH_LIST.forEach((path, httpMethod) -> auth.mvcMatchers(httpMethod, path).hasAnyRole(rolesAboveAdmin));
                    auth.anyRequest().authenticated();
                })
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class)
                .addFilterBefore(jwtExceptionFilter, jwtAuthenticationFilter.getClass())
                .exceptionHandling(httpSecurityExceptionHandlingConfigurer -> httpSecurityExceptionHandlingConfigurer
                        .accessDeniedHandler(jwtAccessDeniedHandler)
                        .authenticationEntryPoint(jwtAuthenticationEntryPoint)
                )
                .build();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource(
            @Value("${eatery.web.dev.url}") String eateryWebDevUrl,
            @Value("${eatery.web.prod.url}") String eateryWebProdUrl,
            @Value("${eatery.server.dev.url}") String eateryServerDevUrl,
            @Value("${eatery.server.prod.url}") String eateryServerProdUrl
    ) {
        CorsConfiguration corsConfig = new CorsConfiguration();
        corsConfig.setAllowCredentials(true);
        corsConfig.setAllowedOrigins(List.of(
                "http://localhost", "http://localhost:8080",
                eateryWebDevUrl, eateryWebProdUrl,
                eateryServerDevUrl, eateryServerProdUrl
        ));
        corsConfig.setAllowedMethods(List.of(GET.name(), POST.name(), PUT.name(), DELETE.name(), PATCH.name(), OPTIONS.name()));
        corsConfig.setAllowedHeaders(List.of("*"));
        corsConfig.setExposedHeaders(List.of("*"));

        UrlBasedCorsConfigurationSource corsConfigurationSource = new UrlBasedCorsConfigurationSource();
        corsConfigurationSource.registerCorsConfiguration("/**", corsConfig);
        return corsConfigurationSource;
    }
}
