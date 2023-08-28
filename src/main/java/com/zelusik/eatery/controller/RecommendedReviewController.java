package com.zelusik.eatery.controller;

import com.zelusik.eatery.dto.recommended_review.RecommendedReviewDto;
import com.zelusik.eatery.dto.recommended_review.request.SaveRecommendedReviewsRequest;
import com.zelusik.eatery.dto.recommended_review.response.SaveRecommendedReviewsResponse;
import com.zelusik.eatery.security.UserPrincipal;
import com.zelusik.eatery.service.RecommendedReviewService;
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

import javax.validation.Valid;
import java.net.URI;

@Tag(name = "추천 리뷰 관련 API")
@RequiredArgsConstructor
@RequestMapping("/api/members/recommended-reviews")
@RestController
public class RecommendedReviewController {

    private final RecommendedReviewService recommendedReviewService;

    @Operation(
            summary = "추천 리뷰 설정",
            description = "추천 리뷰를 지정하여 저장합니다.",
            security = @SecurityRequirement(name = "access-token")
    )
    @PostMapping
    public ResponseEntity<SaveRecommendedReviewsResponse> saveRecommendedReviews(
            @AuthenticationPrincipal UserPrincipal userPrincipal,
            @RequestBody @Valid SaveRecommendedReviewsRequest saveRecommendedReviewsRequest
    ) {
        RecommendedReviewDto recommendedReviewDto = recommendedReviewService.saveRecommendedReview(userPrincipal.getMemberId(), saveRecommendedReviewsRequest.getReviewId(), saveRecommendedReviewsRequest.getRanking());
        return ResponseEntity
                .created(URI.create("/api/members/recommended-reviews/" + recommendedReviewDto.getId()))
                .body(SaveRecommendedReviewsResponse.from(recommendedReviewDto));
    }
}