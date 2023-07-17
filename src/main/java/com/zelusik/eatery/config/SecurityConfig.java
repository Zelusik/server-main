package com.zelusik.eatery.config;

import com.zelusik.eatery.constant.member.RoleType;
import com.zelusik.eatery.security.JwtAccessDeniedHandler;
import com.zelusik.eatery.security.JwtAuthenticationEntryPoint;
import com.zelusik.eatery.security.JwtAuthenticationFilter;
import com.zelusik.eatery.security.JwtExceptionFilter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.security.servlet.PathRequest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import java.util.Arrays;
import java.util.Map;

@Slf4j
@RequiredArgsConstructor
@Configuration
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtAuthenticationFilter;
    private final JwtExceptionFilter jwtExceptionFilter;
    private final JwtAccessDeniedHandler jwtAccessDeniedHandler;
    private final JwtAuthenticationEntryPoint jwtAuthenticationEntryPoint;

    private static final String[] AUTH_WHITE_PATHS = {
            "/api/auth/login/**",
            "/api/auth/token",
            "/api/auth/validity"
    };

    private static final Map<String, HttpMethod> MANAGER_AUTH_LIST = Map.of(
            "/api/curation/**", HttpMethod.POST
    );

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        String[] rolesAboveManager = {RoleType.MANAGER.name(), RoleType.ADMIN.name()};

        return http
                .httpBasic().disable()
                .csrf().disable()
                .formLogin().disable()
                .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS).and()   // JWT 기반 인증이기 때문에 session 사용 x
                .authorizeHttpRequests(auth -> {
                    auth.requestMatchers(PathRequest.toStaticResources().atCommonLocations()).permitAll()
                            .mvcMatchers("/swagger-ui/**", "/v3/api-docs/**", "/actuator/**").permitAll()
                            .mvcMatchers(HttpMethod.GET, "/api/curation").permitAll();
                    Arrays.stream(AUTH_WHITE_PATHS).forEach(authWhiteListElem -> auth.mvcMatchers(authWhiteListElem).permitAll());
                    MANAGER_AUTH_LIST.forEach((path, httpMethod) -> auth.mvcMatchers(httpMethod, path).hasAnyRole(rolesAboveManager));
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
}
