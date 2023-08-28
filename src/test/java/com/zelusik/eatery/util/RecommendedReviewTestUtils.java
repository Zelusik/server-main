package com.zelusik.eatery.util;

import com.zelusik.eatery.domain.RecommendedReview;
import com.zelusik.eatery.domain.member.Member;
import com.zelusik.eatery.domain.review.Review;
import com.zelusik.eatery.dto.recommended_review.RecommendedReviewDto;

import java.time.LocalDateTime;

public class RecommendedReviewTestUtils {

    public static RecommendedReview createRecommendedReview(long id, Member member, Review review, short ranking) {
        return RecommendedReview.of(
                id,
                member,
                review,
                ranking,
                LocalDateTime.now(),
                LocalDateTime.now()
        );
    }

    public static RecommendedReviewDto createRecommendedReviewDto(long id, long memberId, long reviewId, short ranking) {
        return new RecommendedReviewDto(id, memberId, reviewId, ranking);
    }
}
