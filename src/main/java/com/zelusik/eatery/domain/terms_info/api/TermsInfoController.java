package com.zelusik.eatery.domain.terms_info.api;

import com.zelusik.eatery.domain.terms_info.dto.TermsInfoDto;
import com.zelusik.eatery.domain.terms_info.dto.request.AgreeToTermsRequest;
import com.zelusik.eatery.domain.terms_info.dto.response.AgreeToTermsResponse;
import com.zelusik.eatery.domain.terms_info.service.TermsInfoCommandService;
import com.zelusik.eatery.global.auth.UserPrincipal;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
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

import static com.zelusik.eatery.global.common.constant.EateryConstants.API_MINOR_VERSION_HEADER_NAME;

@Tag(name = "회원 약관 관련 API")
@RequiredArgsConstructor
@RequestMapping("/api")
@RestController
public class TermsInfoController {

    private final TermsInfoCommandService termsInfoCommandService;

    @Operation(
            summary = "약관 동의",
            description = "<p><strong>Latest version: v1.1</strong>" +
                          "<p>전체 약관에 대한 동의/비동의 결과를 제출한다.",
            security = @SecurityRequirement(name = "access-token")
    )
    @ApiResponses({
            @ApiResponse(description = "OK", responseCode = "201"),
            @ApiResponse(description = "[1200] 필수 이용 약관이 `false`로 전달된 경우", responseCode = "422", content = @Content)
    })
    @PostMapping(value = "/v1/members/terms", headers = API_MINOR_VERSION_HEADER_NAME + "=1")
    public ResponseEntity<AgreeToTermsResponse> saveTermsInfoV1_1(
            @AuthenticationPrincipal UserPrincipal userPrincipal,
            @Valid @RequestBody AgreeToTermsRequest request
    ) {
        TermsInfoDto termsInfoDto = termsInfoCommandService.saveTermsInfo(userPrincipal.getMemberId(), request);
        return ResponseEntity
                .created(URI.create("/api/v1/members/terms/" + termsInfoDto.getId()))
                .body(AgreeToTermsResponse.from(termsInfoDto));
    }
}
