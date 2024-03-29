package com.zelusik.eatery.domain.terms_info.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.validation.constraints.AssertTrue;
import javax.validation.constraints.NotNull;

@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PRIVATE)
@Getter
public class AgreeToTermsRequest {

    @Schema(description = "미성년자 여부. 만 14세 이상이라면 true", example = "true")
    @AssertTrue
    @NotNull
    private Boolean isNotMinor;

    @Schema(description = "서비스 이용약관 동의", example = "true")
    @AssertTrue
    @NotNull
    private Boolean service;

    @Schema(description = "개인정보 수집/이용 동의", example = "true")
    @AssertTrue
    @NotNull
    private Boolean userInfo;

    @Schema(description = "위치 정보 제공 동의", example = "true")
    @NotNull
    private Boolean locationInfo;

    @Schema(description = "마케팅 수신 동의", example = "false")
    @NotNull
    private Boolean marketingReception;
}
