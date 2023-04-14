package com.zelusik.eatery.repository.review;

import com.querydsl.jpa.impl.JPAQueryFactory;
import com.zelusik.eatery.domain.place.Place;
import com.zelusik.eatery.domain.review.QReviewImage;
import com.zelusik.eatery.domain.review.ReviewImage;
import lombok.RequiredArgsConstructor;

import java.util.List;

import static com.zelusik.eatery.domain.place.QPlace.place;
import static com.zelusik.eatery.domain.review.QReview.review;
import static com.zelusik.eatery.domain.review.QReviewImage.reviewImage;

@RequiredArgsConstructor
public class ReviewImageQuerydslRepositoryImpl implements ReviewImageQuerydslRepository {

    private final JPAQueryFactory queryFactory;

    @Override
    public List<ReviewImage> findLatest3ByPlace(Place cond) {
        return queryFactory.select(reviewImage)
                .from(review)
                .join(review.place, place)
                .join(review.reviewImages, reviewImage)
                .where(review.place.eq(cond)
                        .and(review.deletedAt.isNull()))
                .orderBy(reviewImage.createdAt.desc())
                .limit(3)
                .fetch();
    }
}
