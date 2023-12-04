package com.zelusik.eatery.global.apple.dto;

import com.zelusik.eatery.global.common.constant.EateryConstants;
import com.zelusik.eatery.domain.member.constant.Gender;
import com.zelusik.eatery.domain.member.constant.LoginType;
import com.zelusik.eatery.domain.member.constant.RoleType;
import com.zelusik.eatery.domain.member.dto.MemberDto;
import com.zelusik.eatery.global.util.NicknameGenerator;
import io.jsonwebtoken.Claims;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Set;

@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Getter
public class AppleOAuthUserInfo {

    private String sub;
    private String email;
    private Boolean emailVerified;
    private Boolean isPrivateEmail;

    public static AppleOAuthUserInfo from(Claims claims) {
        return new AppleOAuthUserInfo(
                claims.getSubject(),
                claims.get("email", String.class),
                Boolean.valueOf(claims.get("email_verified", String.class)),
                Boolean.valueOf(claims.get("is_private_email", String.class))
        );
    }

    public MemberDto toMemberDto(String name, Set<RoleType> roleTypes) {
        return new MemberDto(
                EateryConstants.defaultProfileImageUrl,
                EateryConstants.defaultProfileThumbnailImageUrl,
                this.getSub(),
                LoginType.APPLE,
                roleTypes,
                this.getEmail(),
                name != null ? name : NicknameGenerator.generateRandomNickname(),
                null,
                Gender.ETC
        );
    }
}
