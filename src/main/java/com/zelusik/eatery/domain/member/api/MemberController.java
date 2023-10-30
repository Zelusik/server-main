package com.zelusik.eatery.domain.member.api;

import com.zelusik.eatery.domain.favorite_food_category.dto.request.FavoriteFoodCategoriesUpdateRequest;
import com.zelusik.eatery.domain.favorite_food_category.dto.response.UpdateFavoriteFoodCategoriesResponse;
import com.zelusik.eatery.domain.member.dto.MemberDto;
import com.zelusik.eatery.domain.member.dto.MemberWithProfileInfoDto;
import com.zelusik.eatery.domain.member.dto.request.MemberUpdateRequest;
import com.zelusik.eatery.domain.member.dto.response.*;
import com.zelusik.eatery.domain.member.service.MemberCommandService;
import com.zelusik.eatery.domain.member.service.MemberQueryService;
import com.zelusik.eatery.domain.member_deletion_survey.dto.request.MemberDeletionSurveyRequest;
import com.zelusik.eatery.domain.member_deletion_survey.dto.response.MemberDeletionSurveyResponse;
import com.zelusik.eatery.global.common.dto.response.SliceResponse;
import com.zelusik.eatery.global.auth.UserPrincipal;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Slice;
import org.springframework.http.MediaType;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import javax.validation.constraints.NotEmpty;

import static com.zelusik.eatery.global.common.constant.EateryConstants.API_MINOR_VERSION_HEADER_NAME;

@Tag(name = "회원 관련 API")
@RequiredArgsConstructor
@RequestMapping("/api")
@RestController
public class MemberController {

    private final MemberCommandService memberCommandService;
    private final MemberQueryService memberQueryService;

    @Operation(
            summary = "내 정보 조회",
            description = "<p><strong>Latest version: v1.1</strong>" +
                          "<p>내 정보를 조회합니다.",
            security = @SecurityRequirement(name = "access-token")
    )
    @GetMapping(value = "/v1/members/me", headers = API_MINOR_VERSION_HEADER_NAME + "=1")
    public GetMyInfoResponse getMyInfoV1_1(@AuthenticationPrincipal UserPrincipal userPrincipal) {
        return GetMyInfoResponse.from(memberQueryService.findDtoById(userPrincipal.getMemberId()));
    }

    @Operation(
            summary = "내 프로필 정보 조회",
            description = """
                    <p><strong>Latest version: v1.1</strong>
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
    @GetMapping(value = "/v1/members/me/profile", headers = API_MINOR_VERSION_HEADER_NAME + "=1")
    public GetMyProfileInfoResponse getMyProfileInfoV1_1(@AuthenticationPrincipal UserPrincipal userPrincipal) {
        return GetMyProfileInfoResponse.from(memberQueryService.getMemberProfileInfoById(userPrincipal.getMemberId()));
    }

    @Operation(
            summary = "회원 프로필 정보 조회",
            description = """
                    <p><strong>Latest version: v1.1</strong>
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
    @GetMapping(value = "/v1/members/{memberId}/profile", headers = API_MINOR_VERSION_HEADER_NAME + "=1")
    public GetMemberProfileInfoResponse getMemberProfileInfoV1_1(
            @AuthenticationPrincipal UserPrincipal userPrincipal,
            @PathVariable Long memberId
    ) {
        MemberWithProfileInfoDto memberWithProfileInfoDto = memberQueryService.getMemberProfileInfoById(memberId);
        return GetMemberProfileInfoResponse.from(userPrincipal.getMemberId(), memberWithProfileInfoDto);
    }

    @Operation(
            summary = "키워드로 회원 검색하기",
            description = "<p><strong>Latest version: v1.1</strong>" +
                          "<p>검색 키워드를 전달받아 회원을 검색한다.",
            security = @SecurityRequirement(name = "access-token")
    )
    @GetMapping(value = "/v1/members/search", headers = API_MINOR_VERSION_HEADER_NAME + "=1")
    public SliceResponse<SearchMembersByKeywordResponse> searchMembersByKeywordV1_1(
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
        Slice<MemberDto> memberDtos = memberQueryService.searchDtosByKeyword(keyword, PageRequest.of(page, size));
        return new SliceResponse<SearchMembersByKeywordResponse>().from(memberDtos.map(SearchMembersByKeywordResponse::from));
    }

    @Operation(
            summary = "내 정보 수정",
            description = "<p><strong>Latest version: v1.1</strong>" +
                          "<p>내 정보를 수정합니다." +
                          "<p>프로필 이미지는 수정하고자 하는 경우에만 요청해야 하고, 수정하지 않는 경우 요청 데이터에서 제외해야합니다.",
            security = @SecurityRequirement(name = "access-token")
    )
    @PutMapping(value = "/v1/members", headers = API_MINOR_VERSION_HEADER_NAME + "=1", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public MemberResponse updateMemberV1_1(
            @AuthenticationPrincipal UserPrincipal userPrincipal,
            @ModelAttribute @Valid MemberUpdateRequest memberUpdateRequest
    ) {
        MemberDto updatedMember = memberCommandService.update(userPrincipal.getMemberId(), memberUpdateRequest);
        return MemberResponse.from(updatedMember);
    }

    @Operation(
            summary = "선호하는 음식 카테고리(음식 취향) 변경",
            description = "<p><strong>Latest version: v1.1</strong>" +
                          "<p>선호하는 음식 카테고리(음식 취향)를 변경한다.",
            security = @SecurityRequirement(name = "access-token")
    )
    @PutMapping(value = "/v1/members/favorite-food", headers = API_MINOR_VERSION_HEADER_NAME + "=1")
    public UpdateFavoriteFoodCategoriesResponse updateFavoriteFoodCategoriesV1_1(
            @AuthenticationPrincipal UserPrincipal userPrincipal,
            @Valid @RequestBody FavoriteFoodCategoriesUpdateRequest request
    ) {
        MemberDto updatedMemberDto = memberCommandService.updateFavoriteFoodCategories(userPrincipal.getMemberId(), request.getFavoriteFoodCategories());
        return UpdateFavoriteFoodCategoriesResponse.from(updatedMemberDto);
    }

    @Operation(
            summary = "회원 탈퇴",
            description = "<p><strong>Latest version: v1.1</strong>" +
                          "<p>회원 탈퇴를 진행한다.",
            security = @SecurityRequirement(name = "access-token")
    )
    @DeleteMapping(value = "/v1/members", headers = API_MINOR_VERSION_HEADER_NAME + "=1")
    public MemberDeletionSurveyResponse deleteV1_1(
            @AuthenticationPrincipal UserPrincipal userPrincipal,
            @Valid @RequestBody MemberDeletionSurveyRequest memberDeletionSurveyRequest
    ) {
        return MemberDeletionSurveyResponse.from(
                memberCommandService.delete(
                        userPrincipal.getMemberId(),
                        memberDeletionSurveyRequest.getSurveyType()
                )
        );
    }
}
