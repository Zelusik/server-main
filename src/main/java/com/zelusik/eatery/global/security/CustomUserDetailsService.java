package com.zelusik.eatery.global.security;

import com.zelusik.eatery.app.service.MemberService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.core.userdetails.UserDetailsService;

@Configuration
public class CustomUserDetailsService {

    @Bean
    public UserDetailsService userDetailsService(MemberService memberService) {
        return username -> UserPrincipal.of(memberService.findMemberById(Long.valueOf(username)));
    }
}
