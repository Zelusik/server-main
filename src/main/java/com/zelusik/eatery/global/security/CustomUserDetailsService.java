package com.zelusik.eatery.global.security;

import com.zelusik.eatery.domain.member.service.MemberQueryService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.core.userdetails.UserDetailsService;

@Configuration
public class CustomUserDetailsService {

    @Bean
    public UserDetailsService userDetailsService(MemberQueryService memberQueryService) {
        return username -> UserPrincipal.of(memberQueryService.findDtoById(Long.valueOf(username)));
    }
}
