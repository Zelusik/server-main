package com.zelusik.eatery.global.auth.dto.response;

import com.zelusik.eatery.domain.member.constant.LoginType;
import com.zelusik.eatery.global.auth.dto.JwtTokenDto;
import com.zelusik.eatery.domain.member.dto.MemberDto;
import com.zelusik.eatery.domain.terms_info.dto.TermsInfoDto;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.lang.Nullable;

import java.time.LocalDateTime;

@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PRIVATE)
@Getter
public class LoginResponse {

    @Schema(description = "로그인 유저 정보")
    private LoggedInMemberResponse loggedInMember;

    @Schema(description = "Token 정보")
    private TokenResponse tokens;

    public static LoginResponse from(MemberDto memberDto, @Nullable TermsInfoDto termsInfoDto, JwtTokenDto jwtTokenDto) {
        return new LoginResponse(LoggedInMemberResponse.from(memberDto, termsInfoDto), TokenResponse.from(jwtTokenDto));
    }

    @AllArgsConstructor(access = AccessLevel.PRIVATE)
    @NoArgsConstructor(access = AccessLevel.PRIVATE)
    @Getter
    private static class LoggedInMemberResponse {

        @Schema(description = "회원의 id(PK)", example = "1")
        private Long id;

        @Schema(description = "약관 동의 정보")
        private TermsInfoResponse termsInfo;

        @Schema(description = "로그인 유형", example = "KAKAO")
        private LoginType loginType;

        @Schema(description = "닉네임", example = "우기")
        private String nickname;

        private static LoggedInMemberResponse from(MemberDto memberDto, TermsInfoDto termsInfoDto) {
            return new LoggedInMemberResponse(
                    memberDto.getId(),
                    TermsInfoResponse.from(termsInfoDto),
                    memberDto.getLoginType(),
                    memberDto.getNickname()
            );
        }

        @AllArgsConstructor(access = AccessLevel.PRIVATE)
        @NoArgsConstructor(access = AccessLevel.PRIVATE)
        @Getter
        private static class TermsInfoResponse {

            @Schema(description = "PK of terms info", example = "10")
            private Long id;

            @Schema(description = "미성년자(14세 미만) 여부", example = "true")
            private Boolean isNotMinor;

            @Schema(description = "서비스 약관 동의 여부", example = "true")
            private Boolean service;

            @Schema(description = "서비스 약관 동의 갱신 일시", example = "2023-02-27T08:29:11.091Z")
            private LocalDateTime serviceUpdatedAt;

            @Schema(description = "사용자 정보 약관 동의 여부", example = "true")
            private Boolean userInfo;

            @Schema(description = "사용자 정보 약관 동의 갱신 일시", example = "2023-02-27T08:29:11.091Z")
            private LocalDateTime userInfoUpdatedAt;

            @Schema(description = "위치 정보 수집 약관 동의 갱신 일시", example = "true")
            private Boolean locationInfo;

            @Schema(description = "위치 정보 수집 약관 동의 갱신 일시", example = "2023-02-27T08:29:11.091Z")
            private LocalDateTime locationInfoUpdatedAt;

            @Schema(description = "마케팅 수신 약관 동의 여부", example = "false")
            private Boolean marketingReception;

            @Schema(description = "마케팅 수신 약관 동의 갱신 일시", example = "2023-02-27T08:29:11.091Z")
            private LocalDateTime marketingReceptionUpdatedAt;

            private static TermsInfoResponse from(TermsInfoDto termsInfoDto) {
                if (termsInfoDto == null) {
                    return null;
                }
                return new TermsInfoResponse(
                        termsInfoDto.getId(),
                        termsInfoDto.getIsNotMinor(),
                        termsInfoDto.getService(), termsInfoDto.getServiceUpdatedAt(),
                        termsInfoDto.getUserInfo(), termsInfoDto.getUserInfoUpdatedAt(),
                        termsInfoDto.getLocationInfo(), termsInfoDto.getLocationInfoUpdatedAt(),
                        termsInfoDto.getMarketingReception(), termsInfoDto.getMarketingReceptionUpdatedAt()
                );
            }
        }

    }

    @AllArgsConstructor(access = AccessLevel.PRIVATE)
    @NoArgsConstructor(access = AccessLevel.PRIVATE)
    @Getter
    private static class TokenResponse {

        @Schema(description = "Access token", example = "eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJzdWIiOiIxIiwicm9sZSI6IlJPTEVfVVNFUiIsImxvZ2luVHlwZSI6IktBS0FPIiwiaWF0IjoxNjc3NDg0NzExLCJleHAiOjE2Nzc1Mjc5MTF9.eM2R_mMRqkPUsMmJN_vm2lAsIGownPJZ6Xu47K6ujrI")
        private String accessToken;

        @Schema(description = "Access token 만료 시각", example = "2023-02-28T17:13:55.473")
        private LocalDateTime accessTokenExpiresAt;

        @Schema(description = "Refresh token", example = "eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJzdWIiOiIxIiwicm9sZSI6IlJPTEVfVVNFUiIsImxvZ2luVHlwZSI6IktBS0FPIiwiaWF0IjoxNjc3NDg0NzExLCJleHAiOjE2Nzg2OTQzMTF9.QCq8dj7yet9SKCzt9APu73yaM-_Fx7mowtkl5-bOd64")
        private String refreshToken;

        @Schema(description = "Refresh token 만료 시각", example = "2023-03-30T05:13:55.473")
        private LocalDateTime refreshTokenExpiresAt;

        public static TokenResponse from(JwtTokenDto jwtTokenDto) {
            return new TokenResponse(
                    jwtTokenDto.accessToken(),
                    jwtTokenDto.accessTokenExpiresAt(),
                    jwtTokenDto.refreshToken(),
                    jwtTokenDto.refreshTokenExpiresAt()
            );
        }
    }
}
