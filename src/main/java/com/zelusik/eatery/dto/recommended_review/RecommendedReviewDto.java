package com.zelusik.eatery.dto.recommended_review;

import com.zelusik.eatery.domain.RecommendedReview;
import com.zelusik.eatery.dto.review.ReviewDto;
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
