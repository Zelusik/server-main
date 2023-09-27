package com.zelusik.eatery.domain.recommended_review.dto;

import com.zelusik.eatery.domain.recommended_review.entity.RecommendedReview;
import com.zelusik.eatery.domain.review.dto.ReviewDto;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PRIVATE)
@Getter
public class RecommendedReviewDto {

    private Long id;
    private Long memberId;
    private ReviewDto review;
    private Short ranking;

    public static RecommendedReviewDto fromWithoutReviewWriterAndPlace(RecommendedReview entity) {
        return new RecommendedReviewDto(
                entity.getId(),
                entity.getMember().getId(),
                ReviewDto.from(entity.getReview(), List.of()),
                entity.getRanking()
        );
    }

    public static RecommendedReviewDto from(RecommendedReview entity, boolean placeMarkingStatus) {
        return new RecommendedReviewDto(
                entity.getId(),
                entity.getMember().getId(),
                ReviewDto.from(entity.getReview(), placeMarkingStatus),
                entity.getRanking()
        );
    }
}
