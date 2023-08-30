package com.zelusik.eatery.repository.review;

import com.querydsl.core.Tuple;
import com.querydsl.core.types.Predicate;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.JPAExpressions;
import com.querydsl.jpa.JPQLQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.zelusik.eatery.constant.review.ReviewEmbedOption;
import com.zelusik.eatery.domain.QBookmark;
import com.zelusik.eatery.domain.review.Review;
import com.zelusik.eatery.dto.review.ReviewDto;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import static com.zelusik.eatery.constant.review.ReviewEmbedOption.PLACE;
import static com.zelusik.eatery.domain.member.QMember.member;
import static com.zelusik.eatery.domain.place.QPlace.place;
import static com.zelusik.eatery.domain.review.QReview.review;

@RequiredArgsConstructor
public class ReviewRepositoryQCustomImpl implements ReviewRepositoryQCustom {

    private final JPAQueryFactory queryFactory;

    @Override
    public Slice<ReviewDto> findDtos(Long loginMemberId, Long writerId, Long placeId, List<ReviewEmbedOption> embed, Pageable pageable) {
        List<Predicate> conditions = new ArrayList<>();
        conditions.add(isNotDeleted());
        conditions.add(writerFilteringCondition(writerId));
        conditions.add(placeFilteringCondition(placeId));

        List<ReviewDto> content = new ArrayList<>();
        if (embed.contains(PLACE)) {
            List<Tuple> result = queryFactory
                    .select(review, isMarkedPlace(loginMemberId))
                    .from(review)
                    .join(review.writer, member).fetchJoin()
                    .join(review.place, place).fetchJoin()
                    .where(conditions.toArray(new Predicate[0]))
                    .orderBy(review.createdAt.desc())
                    .offset(pageable.getOffset())
                    .limit(pageable.getPageSize() + 1)
                    .fetch();
            content.addAll(result.stream()
                    .map(tuple -> ReviewDto.from(
                            Objects.requireNonNull(tuple.get(review)),
                            embed,
                            Objects.requireNonNull(tuple.get(isMarkedPlace(loginMemberId)))
                    )).toList());
        } else {
            List<Review> result = queryFactory
                    .select(review)
                    .from(review)
                    .join(review.writer, member).fetchJoin()
                    .join(review.place, place)
                    .where(conditions.toArray(new Predicate[0]))
                    .orderBy(review.createdAt.desc())
                    .offset(pageable.getOffset())
                    .limit(pageable.getPageSize() + 1)
                    .fetch();
            content.addAll(result.stream()
                    .map(review -> ReviewDto.from(review, embed))
                    .toList());
        }

        boolean hasNext = false;
        if (content.size() > pageable.getPageSize()) {
            content.remove(pageable.getPageSize());
            hasNext = true;
        }

        return new SliceImpl<>(content, PageRequest.of(pageable.getPageNumber(), pageable.getPageSize(), Sort.by(Sort.Order.desc("createdAt"))), hasNext);
    }

    private static JPQLQuery<Boolean> isMarkedPlace(Long loginMemberId) {
        QBookmark subBookmark = new QBookmark("bookmark");
        return JPAExpressions
                .select(subBookmark.count().goe(1))
                .from(subBookmark)
                .where(subBookmark.member.id.eq(loginMemberId)
                        .and(subBookmark.place.eq(review.place)));
    }

    private static BooleanExpression isNotDeleted() {
        return review.deletedAt.isNull();
    }

    private BooleanExpression writerFilteringCondition(Long writerId) {
        return writerId != null ? member.id.eq(writerId) : null;
    }

    private BooleanExpression placeFilteringCondition(Long placeId) {
        return placeId != null ? place.id.eq(placeId) : null;
    }
}
