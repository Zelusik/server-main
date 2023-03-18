package com.zelusik.eatery.app.controller;

import com.zelusik.eatery.app.dto.member.request.TermsAgreeRequest;
import com.zelusik.eatery.app.dto.terms_info.response.TermsInfoResponse;
import com.zelusik.eatery.app.service.MemberService;
import com.zelusik.eatery.global.security.UserPrincipal;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.net.URI;

@Tag(name = "회원 관련 API")
@RequiredArgsConstructor
@RequestMapping("/api/members")
@RestController
public class MemberController {

    private final MemberService memberService;

    @Operation(
            summary = "약관 동의",
            description = "<p>전체 약관에 대한 동의/비동의 결과를 제출한다.",
            security = @SecurityRequirement(name = "access-token")
    )
    @PostMapping("/terms")
    public ResponseEntity<TermsInfoResponse> agree(
            @AuthenticationPrincipal UserPrincipal userPrincipal,
            @RequestBody TermsAgreeRequest request
    ) {
        TermsInfoResponse response = TermsInfoResponse.from(memberService.agreeToTerms(userPrincipal.getMemberId(), request));

        return ResponseEntity
                .created(URI.create("/api/members/terms/" + response.getId()))
                .body(response);
    }
}
