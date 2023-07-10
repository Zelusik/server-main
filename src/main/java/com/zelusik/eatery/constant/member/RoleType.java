package com.zelusik.eatery.constant.member;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public enum RoleType {

    USER("ROLE_USER", "사용자"),
    MANAGER("ROLE_MANAGER", "운영자"),
    ADMIN("ROLE_ADMIN", "관리자"),
    ;

    private final String name;
    private final String description;
}
