package com.zelusik.eatery.domain.recommended_review.dto.response;

import com.zelusik.eatery.domain.recommended_review.dto.RecommendedReviewDto;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PRIVATE)
@Getter
public class BatchUpdateRecommendedReviewsResponse {

    private List<RecommendedReviewResponse> recommendedReviews;

    public static BatchUpdateRecommendedReviewsResponse from(List<RecommendedReviewDto> recommendedReviewWithPlaceDtos) {
        return new BatchUpdateRecommendedReviewsResponse(
                recommendedReviewWithPlaceDtos.stream()
                        .map(RecommendedReviewResponse::from)
                        .toList()
        );
    }

    @AllArgsConstructor(access = AccessLevel.PRIVATE)
    @NoArgsConstructor(access = AccessLevel.PRIVATE)
    @Getter
    private static class RecommendedReviewResponse {

        @Schema(description = "PK of recommended review", example = "19")
        private Long id;

        @Schema(description = "추천 리뷰로 등록한 리뷰의 id(PK)", example = "2")
        private Long reviewId;

        @Schema(description = "순위", example = "1")
        private Short ranking;

        public static RecommendedReviewResponse from(RecommendedReviewDto recommendedReviewDto) {
            return new RecommendedReviewResponse(
                    recommendedReviewDto.getId(),
                    recommendedReviewDto.getReview().getId(),
                    recommendedReviewDto.getRanking()
            );
        }
    }
}
