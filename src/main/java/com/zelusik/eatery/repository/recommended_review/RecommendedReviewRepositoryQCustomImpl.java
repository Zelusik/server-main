package com.zelusik.eatery.repository.recommended_review;

import com.querydsl.core.Tuple;
import com.querydsl.jpa.JPAExpressions;
import com.querydsl.jpa.JPQLQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.zelusik.eatery.dto.recommended_review.RecommendedReviewDto;
import lombok.RequiredArgsConstructor;

import java.util.List;
import java.util.Objects;

import static com.zelusik.eatery.domain.QBookmark.bookmark;
import static com.zelusik.eatery.domain.QRecommendedReview.recommendedReview;
import static com.zelusik.eatery.domain.member.QMember.member;
import static com.zelusik.eatery.domain.place.QPlace.place;
import static com.zelusik.eatery.domain.review.QReview.review;

@RequiredArgsConstructor
public class RecommendedReviewRepositoryQCustomImpl implements RecommendedReviewRepositoryQCustom {

    private final JPAQueryFactory queryFactory;

    @Override
    public List<RecommendedReviewDto> findAllDtosWithPlaceMarkedStatusByMemberId(long memberId) {
        List<Tuple> tuples = queryFactory
                .select(recommendedReview, subQueryForGetPlaceMarkedStatus())
                .from(recommendedReview)
                .join(recommendedReview.member, member).fetchJoin()
                .join(recommendedReview.review, review).fetchJoin()
                .join(review.place, place).fetchJoin()
                .where(member.id.eq(memberId))
                .orderBy(recommendedReview.ranking.asc())
                .fetch();

        if (tuples == null || tuples.isEmpty()) {
            return List.of();
        }

        return tuples.stream()
                .map(tuple -> RecommendedReviewDto.from(
                        Objects.requireNonNull(tuple.get(recommendedReview)),
                        Boolean.TRUE.equals(tuple.get(subQueryForGetPlaceMarkedStatus()))
                ))
                .toList();
    }

    private static JPQLQuery<Boolean> subQueryForGetPlaceMarkedStatus() {
        return JPAExpressions
                .select(bookmark.count().eq(1L))
                .from(bookmark)
                .where(bookmark.member.eq(member)
                        .and(bookmark.place.eq(place)));
    }
}
