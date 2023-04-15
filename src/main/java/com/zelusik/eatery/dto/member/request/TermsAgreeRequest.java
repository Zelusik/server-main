package com.zelusik.eatery.dto.member.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.validation.constraints.AssertTrue;

@AllArgsConstructor(access = AccessLevel.PRIVATE)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public class TermsAgreeRequest {

    @Schema(description = "미성년자 여부. 만 14세 이상이라면 true", example = "true")
    @AssertTrue
    private Boolean isNotMinor;

    @Schema(description = "서비스 이용약관 동의", example = "true")
    @AssertTrue
    private Boolean service;

    @Schema(description = "개인정보 수집/이용 동의", example = "true")
    @AssertTrue
    private Boolean userInfo;

    @Schema(description = "위치 정보 제공 동의", example = "true")
    private Boolean locationInfo;

    @Schema(description = "마케팅 수신 동의", example = "false")
    private Boolean marketingReception;

    public static TermsAgreeRequest of(Boolean isNotMinor, Boolean service, Boolean userInfo, Boolean locationInfo, Boolean marketingReception) {
        return new TermsAgreeRequest(isNotMinor, service, userInfo, locationInfo, marketingReception);
    }
}