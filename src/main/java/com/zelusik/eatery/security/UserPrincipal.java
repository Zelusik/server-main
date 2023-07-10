package com.zelusik.eatery.security;

import com.zelusik.eatery.constant.member.RoleType;
import com.zelusik.eatery.dto.member.MemberDto;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.stream.Collectors;

@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class UserPrincipal implements UserDetails {

    private MemberDto memberDto;

    public static UserPrincipal of(MemberDto memberDto) {
        return new UserPrincipal(memberDto);
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return memberDto.getRoleTypes().stream()
                .map(RoleType::getName)
                .map(SimpleGrantedAuthority::new)
                .collect(Collectors.toUnmodifiableSet());
    }

    public Long getMemberId() {
        return memberDto.getId();
    }

    @Override
    public String getUsername() {
        return String.valueOf(getMemberId());
    }

    @Override
    public String getPassword() {
        return memberDto.getSocialUid();
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }
}
