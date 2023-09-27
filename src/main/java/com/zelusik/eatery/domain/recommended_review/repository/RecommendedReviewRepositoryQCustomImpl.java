package com.zelusik.eatery.domain.recommended_review.repository;

import com.querydsl.core.Tuple;
import com.querydsl.jpa.JPAExpressions;
import com.querydsl.jpa.JPQLQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.zelusik.eatery.domain.recommended_review.dto.RecommendedReviewDto;
import lombok.RequiredArgsConstructor;

import java.util.List;
import java.util.Objects;

import static com.zelusik.eatery.domain.bookmark.entity.QBookmark.bookmark;
import static com.zelusik.eatery.domain.member.entity.QMember.member;
import static com.zelusik.eatery.domain.place.entity.QPlace.place;
import static com.zelusik.eatery.domain.recommended_review.entity.QRecommendedReview.recommendedReview;
import static com.zelusik.eatery.domain.review.entity.QReview.review;

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
