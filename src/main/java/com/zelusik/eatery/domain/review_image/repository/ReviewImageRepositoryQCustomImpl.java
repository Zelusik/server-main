package com.zelusik.eatery.domain.review_image.repository;

import com.querydsl.jpa.impl.JPAQueryFactory;
import com.zelusik.eatery.domain.review_image.entity.ReviewImage;
import lombok.RequiredArgsConstructor;

import java.util.List;

import static com.zelusik.eatery.domain.place.entity.QPlace.place;
import static com.zelusik.eatery.domain.review.entity.QReview.review;
import static com.zelusik.eatery.domain.review_image.entity.QReviewImage.reviewImage;

@RequiredArgsConstructor
public class ReviewImageRepositoryQCustomImpl implements ReviewImageRepositoryQCustom {

    private final JPAQueryFactory queryFactory;

    @Override
    public List<ReviewImage> findLatest3ByPlace(Long placeId) {
        return queryFactory.select(reviewImage)
                .from(review)
                .join(review.place, place)
                .join(review.reviewImages, reviewImage)
                .where(review.place.id.eq(placeId)
                        .and(review.deletedAt.isNull()))
                .orderBy(reviewImage.createdAt.desc())
                .limit(3)
                .fetch();
    }
}
