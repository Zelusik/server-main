package com.zelusik.eatery.domain.recommended_review.dto.response;

import com.zelusik.eatery.domain.recommended_review.dto.RecommendedReviewDto;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PRIVATE)
@Getter
public class SaveRecommendedReviewsResponse {

    @Schema(description = "PK of recommended review", example = "19")
    private Long id;

    @Schema(description = "추천 리뷰로 등록한 리뷰의 id(PK)", example = "2")
    private Long reviewId;

    @Schema(description = "순위", example = "1")
    private Short ranking;

    public static SaveRecommendedReviewsResponse from(RecommendedReviewDto recommendedReviewDto) {
        return new SaveRecommendedReviewsResponse(
                recommendedReviewDto.getId(),
                recommendedReviewDto.getReview().getId(),
                recommendedReviewDto.getRanking()
        );
    }
}
