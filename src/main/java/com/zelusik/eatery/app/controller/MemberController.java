package com.zelusik.eatery.app.controller;

import com.zelusik.eatery.app.dto.member.request.FavoriteFoodCategoriesUpdateRequest;
import com.zelusik.eatery.app.dto.member.request.TermsAgreeRequest;
import com.zelusik.eatery.app.dto.member.response.MemberResponse;
import com.zelusik.eatery.app.dto.terms_info.response.TermsInfoResponse;
import com.zelusik.eatery.app.service.MemberService;
import com.zelusik.eatery.global.security.UserPrincipal;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
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

    @Operation(
            summary = "선호하는 음식 카테고리(음식 취향) 변경",
            description = "<p>선호하는 음식 카테고리(음식 취향)를 변경한다.",
            security = @SecurityRequirement(name = "access-key")
    )
    @PatchMapping("/favorite-food")
    public MemberResponse updateFavoriteFoodCategories(
            @AuthenticationPrincipal UserPrincipal userPrincipal,
            @Valid @RequestBody FavoriteFoodCategoriesUpdateRequest request
    ) {
        return MemberResponse.from(memberService.updateFavoriteFoodCategories(userPrincipal.getMemberId(), request.getFavoriteFoodCategories()));
    }
}
