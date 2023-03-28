package com.zelusik.eatery.app.dto.auth;

import com.zelusik.eatery.app.constant.ConstantUtil;
import com.zelusik.eatery.app.constant.member.Gender;
import com.zelusik.eatery.app.constant.member.LoginType;
import com.zelusik.eatery.app.dto.member.MemberDto;
import com.zelusik.eatery.app.util.NicknameGenerator;
import io.jsonwebtoken.Claims;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;

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

    public MemberDto toMemberDto(String name) {
        return MemberDto.of(
                ConstantUtil.defaultProfileImageUrl,
                ConstantUtil.defaultProfileThumbnailImageUrl,
                this.getSub(),
                LoginType.APPLE,
                this.getEmail(),
                name != null ? name : NicknameGenerator.generateRandomNickname(),
                null,
                Gender.ETC
        );
    }
}
