package com.zelusik.eatery.app.controller;

import com.zelusik.eatery.app.constant.FoodCategory;
import com.zelusik.eatery.app.dto.member.MemberDeletionSurveyDto;
import com.zelusik.eatery.app.dto.member.request.FavoriteFoodCategoriesUpdateRequest;
import com.zelusik.eatery.app.dto.member.request.MemberUpdateRequest;
import com.zelusik.eatery.app.dto.member.request.TermsAgreeRequest;
import com.zelusik.eatery.app.dto.member.response.MemberResponse;
import com.zelusik.eatery.app.dto.review.request.MemberDeletionSurveyRequest;
import com.zelusik.eatery.app.dto.terms_info.response.TermsInfoResponse;
import com.zelusik.eatery.app.service.MemberService;
import com.zelusik.eatery.global.security.UserPrincipal;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.net.URI;
import java.util.List;

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
    @ApiResponses({
            @ApiResponse(description = "OK", responseCode = "201", content = @Content(schema = @Schema(implementation = TermsInfoResponse.class))),
            @ApiResponse(description = "[1200] 필수 이용 약관이 `false`로 전달된 경우", responseCode = "422", content = @Content)
    })
    @PostMapping("/terms")
    public ResponseEntity<TermsInfoResponse> agree(
            @Parameter(hidden = true) @AuthenticationPrincipal UserPrincipal userPrincipal,
            @Valid @RequestBody TermsAgreeRequest request
    ) {
        TermsInfoResponse response = TermsInfoResponse.from(memberService.agreeToTerms(userPrincipal.getMemberId(), request));

        return ResponseEntity
                .created(URI.create("/api/members/terms/" + response.getId()))
                .body(response);
    }

    @Operation(
            summary = "내 정보 조회",
            description = "<p>내 정보를 조회합니다.",
            security = @SecurityRequirement(name = "access-token")
    )
    @GetMapping
    public MemberResponse getMyInfo(
            @Parameter(hidden = true) @AuthenticationPrincipal UserPrincipal userPrincipal
    ) {
        return MemberResponse.from(memberService.findDtoById(userPrincipal.getMemberId()));
    }

    @Operation(
            summary = "회원 정보 수정",
            description = "<p>회원 정보를 수정한다." +
                    "<p>프로필 이미지는 수정하고자 하는 경우에만 요청해야 하고, " +
                    "수정하지 않는 경우 보내지 않거나 <code>null</code>로 보내야 한다.",
            security = @SecurityRequirement(name = "access-token")
    )
    @PutMapping
    public MemberResponse updateMember(
            @Parameter(hidden = true) @AuthenticationPrincipal UserPrincipal userPrincipal,
            @Valid @RequestBody MemberUpdateRequest memberUpdateRequest
    ) {
        return MemberResponse.from(
                memberService.updateMember(
                        userPrincipal.getMemberId(),
                        memberUpdateRequest
                )
        );
    }

    @Operation(
            summary = "선호하는 음식 카테고리(음식 취향) 변경",
            description = "<p>선호하는 음식 카테고리(음식 취향)를 변경한다.",
            security = @SecurityRequirement(name = "access-token")
    )
    @PutMapping("/favorite-food")
    public MemberResponse updateFavoriteFoodCategories(
            @Parameter(hidden = true) @AuthenticationPrincipal UserPrincipal userPrincipal,
            @Valid @RequestBody FavoriteFoodCategoriesUpdateRequest request
    ) {
        List<FoodCategory> favoriteFoodCategories = request.getFavoriteFoodCategories().stream()
                .map(FoodCategory::valueOfDescription)
                .toList();
        return MemberResponse.from(memberService.updateFavoriteFoodCategories(userPrincipal.getMemberId(), favoriteFoodCategories));
    }

    @Operation(
            summary = "회원 탈퇴",
            description = "<p>회원 탈퇴를 진행한다.",
            security = @SecurityRequirement(name = "access-token")
    )
    @DeleteMapping
    public MemberDeletionSurveyDto delete(
            @Parameter(hidden = true) @AuthenticationPrincipal UserPrincipal userPrincipal,
            @Valid @RequestBody MemberDeletionSurveyRequest memberDeletionSurveyRequest
    ) {
        return memberService.delete(userPrincipal.getMemberId(), memberDeletionSurveyRequest.getSurveyType());
    }
}
