package com.zelusik.eatery.app.repository.review;

import com.querydsl.jpa.impl.JPAQueryFactory;
import com.zelusik.eatery.app.domain.place.Place;
import com.zelusik.eatery.app.domain.review.ReviewFile;
import lombok.RequiredArgsConstructor;

import java.util.List;

import static com.zelusik.eatery.app.domain.place.QPlace.place;
import static com.zelusik.eatery.app.domain.review.QReview.review;
import static com.zelusik.eatery.app.domain.review.QReviewFile.reviewFile;

@RequiredArgsConstructor
public class ReviewFileQuerydslRepositoryImpl implements ReviewFileQuerydslRepository {

    private final JPAQueryFactory queryFactory;

    @Override
    public List<ReviewFile> findLatest3ByPlace(Place cond) {
        return queryFactory.select(reviewFile)
                .from(review)
                .join(review.place, place)
                .join(review.reviewFiles, reviewFile)
                .where(review.place.eq(cond)
                        .and(review.deletedAt.isNull()))
                .orderBy(reviewFile.createdAt.desc())
                .limit(3)
                .fetch();
    }
}
