package com.zelusik.eatery.dto.apple;

import com.zelusik.eatery.constant.ConstantUtil;
import com.zelusik.eatery.constant.member.Gender;
import com.zelusik.eatery.constant.member.LoginType;
import com.zelusik.eatery.dto.member.MemberDto;
import com.zelusik.eatery.util.NicknameGenerator;
import io.jsonwebtoken.Claims;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Getter
public class AppleOAuthUserResponse {

    private String sub;
    private String email;
    private Boolean emailVerified;
    private Boolean isPrivateEmail;

    public static AppleOAuthUserResponse from(Claims claims) {
        return new AppleOAuthUserResponse(
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
