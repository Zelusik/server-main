package com.zelusik.eatery.app.dto.member.response;

import com.zelusik.eatery.app.constant.member.LoginType;
import com.zelusik.eatery.app.dto.member.MemberDto;
import com.zelusik.eatery.app.dto.terms_info.response.TermsInfoResponse;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Getter
public class LoggedInMemberResponse {

    @Schema(description = "회원의 id(PK)", example = "1")
    private Long id;

    @Schema(description = "약관 동의 정보")
    private TermsInfoResponse termsInfo;

    @Schema(description = "프로필 이미지 url", example = "https://...")
    private String profileImageUrl;

    @Schema(description = "프로필 이미지 썸네일 url", example = "https://...")
    private String profileThumbnailImageUrl;

    @Schema(description = "로그인 유형", example = "KAKAO")
    private LoginType loginType;

    @Schema(description = "이메일", example = "example@kakao.com")
    private String email;

    @Schema(description = "닉네임", example = "우기")
    private String nickname;

    public static LoggedInMemberResponse from(MemberDto memberDto) {
        return new LoggedInMemberResponse(
                memberDto.id(),
                TermsInfoResponse.from(memberDto.termsInfoDto()),
                memberDto.profileImageUrl(),
                memberDto.profileThumbnailImageUrl(),
                memberDto.loginType(),
                memberDto.email(),
                memberDto.nickname()
        );
    }
}
