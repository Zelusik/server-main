package com.zelusik.eatery.controller;

import com.zelusik.eatery.constant.FoodCategoryValue;
import com.zelusik.eatery.dto.SliceResponse;
import com.zelusik.eatery.dto.member.MemberDto;
import com.zelusik.eatery.dto.member.MemberProfileInfoDto;
import com.zelusik.eatery.dto.member.request.FavoriteFoodCategoriesUpdateRequest;
import com.zelusik.eatery.dto.member.request.MemberUpdateRequest;
import com.zelusik.eatery.dto.member.request.TermsAgreeRequest;
import com.zelusik.eatery.dto.member.response.*;
import com.zelusik.eatery.dto.review.request.MemberDeletionSurveyRequest;
import com.zelusik.eatery.dto.terms_info.response.TermsInfoResponse;
import com.zelusik.eatery.security.UserPrincipal;
import com.zelusik.eatery.service.MemberService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Slice;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import javax.validation.constraints.NotEmpty;
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
            @AuthenticationPrincipal UserPrincipal userPrincipal,
            @Valid @RequestBody TermsAgreeRequest request
    ) {
        TermsInfoResponse response = TermsInfoResponse.from(memberService.agreeToTerms(userPrincipal.getMemberId(), request));

        return ResponseEntity
                .created(URI.create("/api/members/terms/" + response.getId()))
                .body(response);
    }

    @Operation(
            summary = "내 정보 조회",
            description = "내 정보를 조회합니다.",
            security = @SecurityRequirement(name = "access-token")
    )
    @GetMapping("/me")
    public GetMyInfoResponse getMyInfo(@AuthenticationPrincipal UserPrincipal userPrincipal) {
        return GetMyInfoResponse.from(memberService.findDtoById(userPrincipal.getMemberId()));
    }

    @Operation(
            summary = "내 프로필 정보 조회",
            description = """
                    <p>내 프로필 정보를 조회합니다.
                    <p>회원 프로필 정보란 다음 항목들을 의미합니다.
                    <ul>
                        <li>회원 정보</li>
                        <li>작성한 리뷰 수</li>
                        <li>영향력</li>
                        <li>팔로워 수</li>
                        <li>팔로잉 수</li>
                        <li>가장 많이 방문한 장소(읍면동)</li>
                        <li>가장 많이 태그된 리뷰 키워드</li>
                        <li>가장 많이 먹은 음식 카테고리</li>
                    </ul>
                    """,
            security = @SecurityRequirement(name = "access-token")
    )
    @GetMapping("/me/profile")
    public GetMyProfileInfoResponse getMyProfileInfo(@AuthenticationPrincipal UserPrincipal userPrincipal) {
        return GetMyProfileInfoResponse.from(memberService.getMemberProfileInfoById(userPrincipal.getMemberId()));
    }

    @Operation(
            summary = "회원 프로필 정보 조회",
            description = """
                    <p>회원 id를 전달받아 일치하는 회원의 프로필 정보를 조회합니다.
                    <p>회원 프로필 정보란 다음 항목들을 의미합니다.
                    <ul>
                        <li>회원 정보</li>
                        <li>작성한 리뷰 수</li>
                        <li>영향력</li>
                        <li>팔로워 수</li>
                        <li>팔로잉 수</li>
                        <li>가장 많이 방문한 장소(읍면동)</li>
                        <li>가장 많이 태그된 리뷰 키워드</li>
                        <li>가장 많이 먹은 음식 카테고리</li>
                    </ul>
                    """,
            security = @SecurityRequirement(name = "access-token")
    )
    @ApiResponses({
            @ApiResponse(description = "OK", responseCode = "200"),
            @ApiResponse(description = "[2000] 전달받은 <code>memberId</code>에 해당하는 회원을 찾을 수 없는 경우", responseCode = "404", content = @Content)
    })
    @GetMapping("/{memberId}/profile")
    public GetMemberProfileInfoResponse getMemberProfileInfo(
            @AuthenticationPrincipal UserPrincipal userPrincipal,
            @PathVariable Long memberId
    ) {
        MemberProfileInfoDto memberProfileInfoDto = memberService.getMemberProfileInfoById(memberId);
        return GetMemberProfileInfoResponse.from(userPrincipal.getMemberId(), memberProfileInfoDto);
    }

    @Operation(
            summary = "키워드로 회원 검색하기",
            description = "검색 키워드를 전달받아 회원을 검색한다.",
            security = @SecurityRequirement(name = "access-token")
    )
    @GetMapping("/search")
    public SliceResponse<SearchMembersByKeywordResponse> searchMembersByKeyword(
            @Parameter(
                    description = "검색 키워드",
                    example = "강남"
            ) @RequestParam @NotEmpty String keyword,
            @Parameter(
                    description = "페이지 번호 (0부터 시작)",
                    example = "0"
            ) @RequestParam(required = false, defaultValue = "0") int page,
            @Parameter(
                    description = "한 페이지에 담긴 데이터의 최대 개수(사이즈)",
                    example = "30"
            ) @RequestParam(required = false, defaultValue = "30") int size
    ) {
        Slice<MemberDto> memberDtos = memberService.searchDtosByKeyword(keyword, PageRequest.of(page, size));
        return new SliceResponse<SearchMembersByKeywordResponse>().from(memberDtos.map(SearchMembersByKeywordResponse::from));
    }

    @Operation(
            summary = "내 정보 수정",
            description = "<p>내 정보를 수정한다." +
                          "<p>프로필 이미지는 수정하고자 하는 경우에만 요청해야 하고, " +
                          "수정하지 않는 경우 보내지 않거나 <code>null</code>로 보내야 한다.",
            security = @SecurityRequirement(name = "access-token")
    )
    @PutMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public MemberResponse updateMember(
            @AuthenticationPrincipal UserPrincipal userPrincipal,
            @Valid @ModelAttribute MemberUpdateRequest memberUpdateRequest
    ) {
        MemberDto updatedMember = memberService.update(userPrincipal.getMemberId(), memberUpdateRequest);
        return MemberResponse.from(updatedMember);
    }

    @Operation(
            summary = "선호하는 음식 카테고리(음식 취향) 변경",
            description = "<p>선호하는 음식 카테고리(음식 취향)를 변경한다.",
            security = @SecurityRequirement(name = "access-token")
    )
    @PutMapping("/favorite-food")
    public MemberResponse updateFavoriteFoodCategories(
            @AuthenticationPrincipal UserPrincipal userPrincipal,
            @Valid @RequestBody FavoriteFoodCategoriesUpdateRequest request
    ) {
        List<FoodCategoryValue> favoriteFoodCategories = request.getFavoriteFoodCategories().stream()
                .map(FoodCategoryValue::valueOfDescription)
                .toList();
        return MemberResponse.from(memberService.updateFavoriteFoodCategories(userPrincipal.getMemberId(), favoriteFoodCategories));
    }

    @Operation(
            summary = "회원 탈퇴",
            description = "<p>회원 탈퇴를 진행한다.",
            security = @SecurityRequirement(name = "access-token")
    )
    @DeleteMapping
    public MemberDeletionSurveyResponse delete(
            @AuthenticationPrincipal UserPrincipal userPrincipal,
            @Valid @RequestBody MemberDeletionSurveyRequest memberDeletionSurveyRequest
    ) {
        return MemberDeletionSurveyResponse.from(
                memberService.delete(
                        userPrincipal.getMemberId(),
                        memberDeletionSurveyRequest.getSurveyType()
                )
        );
    }
}
