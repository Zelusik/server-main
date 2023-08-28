package com.zelusik.eatery.dto.recommended_review;

import com.zelusik.eatery.domain.RecommendedReview;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PRIVATE)
@Getter
public class RecommendedReviewDto {

    private Long id;
    private Long memberId;
    private Long reviewId;
    private Short ranking;

    public static RecommendedReviewDto from(RecommendedReview entity) {
        return new RecommendedReviewDto(
                entity.getId(),
                entity.getMember().getId(),
                entity.getReview().getId(),
                entity.getRanking()
        );
    }
}
