package com.zelusik.eatery.app.dto.member.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@AllArgsConstructor(access = AccessLevel.PRIVATE)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public class TermsAgreeRequest {

    @Schema(description = "미성년자 여부. 만 14세 이상이라면 false", example = "false")
    private Boolean isMinor;

    @Schema(description = "서비스 이용약관 동의", example = "true")
    private Boolean service;

    @Schema(description = "개인정보 수집/이용 동의", example = "true")
    private Boolean userInfo;

    @Schema(description = "위치 정보 제공 동의", example = "true")
    private Boolean locationInfo;

    @Schema(description = "마케팅 수신 동의", example = "false")
    private Boolean marketingReception;

    public static TermsAgreeRequest of(Boolean isMinor, Boolean service, Boolean userInfo, Boolean locationInfo, Boolean marketingReception) {
        return new TermsAgreeRequest(isMinor, service, userInfo, locationInfo, marketingReception);
    }
}
