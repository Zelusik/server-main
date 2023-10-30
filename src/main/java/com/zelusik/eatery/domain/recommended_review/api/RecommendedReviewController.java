package com.zelusik.eatery.domain.recommended_review.api;

import com.zelusik.eatery.domain.recommended_review.dto.RecommendedReviewDto;
import com.zelusik.eatery.domain.recommended_review.dto.RecommendedReviewWithPlaceDto;
import com.zelusik.eatery.domain.recommended_review.dto.request.BatchUpdateRecommendedReviewsRequest;
import com.zelusik.eatery.domain.recommended_review.dto.request.SaveRecommendedReviewsRequest;
import com.zelusik.eatery.domain.recommended_review.dto.response.BatchUpdateRecommendedReviewsResponse;
import com.zelusik.eatery.domain.recommended_review.dto.response.FindMyRecommendedReviewsResponse;
import com.zelusik.eatery.domain.recommended_review.dto.response.FindRecommendedReviewsResponse;
import com.zelusik.eatery.domain.recommended_review.dto.response.SaveRecommendedReviewsResponse;
import com.zelusik.eatery.domain.recommended_review.service.RecommendedReviewCommandService;
import com.zelusik.eatery.domain.recommended_review.service.RecommendedReviewQueryService;
import com.zelusik.eatery.global.auth.UserPrincipal;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.net.URI;
import java.util.List;

import static com.zelusik.eatery.global.common.constant.EateryConstants.API_MINOR_VERSION_HEADER_NAME;

@Tag(name = "추천 리뷰 관련 API")
@RequiredArgsConstructor
@RequestMapping("/api")
@RestController
public class RecommendedReviewController {

    private final RecommendedReviewCommandService recommendedReviewCommandService;
    private final RecommendedReviewQueryService recommendedReviewQueryService;

    @Operation(
            summary = "추천 리뷰 설정",
            description = "<p><strong>Latest version: v1.1</strong>" +
                          "<p>내 추천 리뷰를 지정하여 저장합니다.",
            security = @SecurityRequirement(name = "access-token")
    )
    @PostMapping(value = "/v1/members/recommended-reviews", headers = API_MINOR_VERSION_HEADER_NAME + "=1")
    public ResponseEntity<SaveRecommendedReviewsResponse> saveRecommendedReviewsV1_1(
            @AuthenticationPrincipal UserPrincipal userPrincipal,
            @RequestBody @Valid SaveRecommendedReviewsRequest saveRecommendedReviewsRequest
    ) {
        RecommendedReviewDto recommendedReviewDto = recommendedReviewCommandService.saveRecommendedReview(userPrincipal.getMemberId(), saveRecommendedReviewsRequest.getReviewId(), saveRecommendedReviewsRequest.getRanking());
        return ResponseEntity
                .created(URI.create("/api/v1/members/recommended-reviews/" + recommendedReviewDto.getId()))
                .body(SaveRecommendedReviewsResponse.from(recommendedReviewDto));
    }

    @Operation(
            summary = "추천 리뷰 조회",
            description = "<p><strong>Latest version: v1.1</strong>" +
                          "<p>전달받은 <code>memberId</code>에 해당하는 회원의 추천 리뷰 정보를 조회합니다.",
            security = @SecurityRequirement(name = "access-token")
    )
    @GetMapping(value = "/v1/members/{memberId}/recommended-reviews", headers = API_MINOR_VERSION_HEADER_NAME + "=1")
    public FindRecommendedReviewsResponse findRecommendedReviewsV1_1(@PathVariable Long memberId) {
        List<RecommendedReviewWithPlaceDto> recommendedReviews = recommendedReviewQueryService.findAllDtosWithPlaceMarkedStatus(memberId);
        return FindRecommendedReviewsResponse.from(recommendedReviews);
    }

    @Operation(
            summary = "내 추천 리뷰 조회",
            description = "<p><strong>Latest version: v1.1</strong>" +
                          "<p>로그인 회원의 추천 리뷰 정보를 조회합니다.",
            security = @SecurityRequirement(name = "access-token")
    )
    @GetMapping(value = "/v1/members/me/recommended-reviews", headers = API_MINOR_VERSION_HEADER_NAME + "=1")
    public FindMyRecommendedReviewsResponse findMyRecommendedReviewsV1_1(@AuthenticationPrincipal UserPrincipal userPrincipal) {
        List<RecommendedReviewWithPlaceDto> recommendedReviews = recommendedReviewQueryService.findAllDtosWithPlaceMarkedStatus(userPrincipal.getMemberId());
        return FindMyRecommendedReviewsResponse.from(recommendedReviews);
    }

    @Operation(
            summary = "추천 리뷰 목록 갱신",
            description = "<p><strong>Latest version: v1.1</strong>" +
                          "<p>내 추천 리뷰를 갱신한다." +
                          "<p>기존 등록된 추천 리뷰 내역을 전부 삭제한 후, 새로 전달받은 추천 리뷰 목록으로 대체한다.",
            security = @SecurityRequirement(name = "access-token")
    )
    @PutMapping(value = "/v1/members/recommended-reviews/batch-update", headers = API_MINOR_VERSION_HEADER_NAME + "=1")
    public BatchUpdateRecommendedReviewsResponse batchUpdateRecommendedReviewsV1_1(
            @AuthenticationPrincipal UserPrincipal userPrincipal,
            @RequestBody @Valid BatchUpdateRecommendedReviewsRequest saveRecommendedReviewsRequest
    ) {
        List<RecommendedReviewDto> recommendedReviewDtos = recommendedReviewCommandService.batchUpdateRecommendedReviews(userPrincipal.getMemberId(), saveRecommendedReviewsRequest);
        return BatchUpdateRecommendedReviewsResponse.from(recommendedReviewDtos);
    }
}
