package com.zelusik.eatery.controller;

import com.zelusik.eatery.dto.member.request.AgreeToTermsRequest;
import com.zelusik.eatery.dto.terms_info.TermsInfoDto;
import com.zelusik.eatery.dto.terms_info.response.AgreeToTermsResponse;
import com.zelusik.eatery.security.UserPrincipal;
import com.zelusik.eatery.service.TermsInfoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.net.URI;

@Tag(name = "회원 약관 관련 API")
@RequiredArgsConstructor
@RequestMapping("/api")
@RestController
public class TermsInfoController {

    private final TermsInfoService termsInfoService;

    @Operation(
            summary = "약관 동의",
            description = "<p>전체 약관에 대한 동의/비동의 결과를 제출한다.",
            security = @SecurityRequirement(name = "access-token")
    )
    @ApiResponses({
            @ApiResponse(description = "OK", responseCode = "201", content = @Content(schema = @Schema(implementation = AgreeToTermsResponse.class))),
            @ApiResponse(description = "[1200] 필수 이용 약관이 `false`로 전달된 경우", responseCode = "422", content = @Content)
    })
    @PostMapping(value = "/v1/members/terms", headers = "Eatery-API-Minor-Version=1")
    public ResponseEntity<AgreeToTermsResponse> saveTermsInfoV1_1(
            @AuthenticationPrincipal UserPrincipal userPrincipal,
            @Valid @RequestBody AgreeToTermsRequest request
    ) {
        TermsInfoDto termsInfoDto = termsInfoService.saveTermsInfo(userPrincipal.getMemberId(), request);
        return ResponseEntity
                .created(URI.create("/api/v1/members/terms/" + termsInfoDto.getId()))
                .body(AgreeToTermsResponse.from(termsInfoDto));
    }
}
