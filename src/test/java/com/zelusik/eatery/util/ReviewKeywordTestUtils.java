package com.zelusik.eatery.util;

import com.zelusik.eatery.constant.review.ReviewKeywordValue;
import com.zelusik.eatery.domain.review.Review;
import com.zelusik.eatery.domain.review.ReviewKeyword;

import java.time.LocalDateTime;

public class ReviewKeywordTestUtils {

    public static ReviewKeyword createNewReviewKeyword(Review review, ReviewKeywordValue reviewKeywordValue) {
        return createReviewKeyword(null, review, reviewKeywordValue);
    }

    public static ReviewKeyword createReviewKeyword(Long reviewKeywordId, Review review, ReviewKeywordValue reviewKeywordValue
    ) {
        return ReviewKeyword.of(
                reviewKeywordId,
                review,
                reviewKeywordValue,
                LocalDateTime.now(),
                LocalDateTime.now()
        );
    }
}
