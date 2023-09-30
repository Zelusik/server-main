package com.zelusik.eatery.domain.recommended_review.dto;

import com.zelusik.eatery.domain.recommended_review.entity.RecommendedReview;
import com.zelusik.eatery.domain.review.dto.ReviewWithPlaceMarkedStatusDto;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

import static com.zelusik.eatery.domain.review.constant.ReviewEmbedOption.PLACE;
import static com.zelusik.eatery.domain.review.constant.ReviewEmbedOption.WRITER;

@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PRIVATE)
@Getter
public class RecommendedReviewDto {

    private Long id;
    private Long memberId;
    private ReviewWithPlaceMarkedStatusDto review;
    private Short ranking;

    public static RecommendedReviewDto fromWithoutReviewWriterAndPlace(RecommendedReview entity) {
        return new RecommendedReviewDto(
                entity.getId(),
                entity.getMember().getId(),
                ReviewWithPlaceMarkedStatusDto.fromWithoutPlace(entity.getReview(), List.of()),
                entity.getRanking()
        );
    }

    public static RecommendedReviewDto from(RecommendedReview entity, boolean placeMarkingStatus) {
        return new RecommendedReviewDto(
                entity.getId(),
                entity.getMember().getId(),
                ReviewWithPlaceMarkedStatusDto.from(entity.getReview(), List.of(WRITER, PLACE), placeMarkingStatus),
                entity.getRanking()
        );
    }
}
