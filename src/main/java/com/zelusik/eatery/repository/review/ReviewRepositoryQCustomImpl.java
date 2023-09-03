package com.zelusik.eatery.repository.review;

import com.querydsl.core.Tuple;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.Predicate;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.CaseBuilder;
import com.querydsl.jpa.JPAExpressions;
import com.querydsl.jpa.JPQLQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.zelusik.eatery.constant.review.ReviewEmbedOption;
import com.zelusik.eatery.domain.QBookmark;
import com.zelusik.eatery.domain.member.FavoriteFoodCategory;
import com.zelusik.eatery.domain.review.Review;
import com.zelusik.eatery.dto.review.ReviewDto;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import static com.zelusik.eatery.constant.review.ReviewEmbedOption.PLACE;
import static com.zelusik.eatery.constant.review.ReviewEmbedOption.WRITER;
import static com.zelusik.eatery.domain.member.QFavoriteFoodCategory.favoriteFoodCategory;
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
        conditions.add(writerEqualFilteringCondition(writerId));
        conditions.add(placeEqualFilteringCondition(placeId));

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

    @Override
    public Slice<ReviewDto> findReviewFeed(long loginMemberId, Pageable pageable) {
        List<FavoriteFoodCategory> favoriteFoodCategories = queryFactory
                .selectFrom(favoriteFoodCategory)
                .where(favoriteFoodCategory.member.id.eq(loginMemberId))
                .fetch();

        List<Tuple> tuples = queryFactory
                .select(review, isMarkedPlace(loginMemberId))
                .from(review)
                .join(review.writer, member).fetchJoin()
                .join(review.place, place).fetchJoin()
                .where(isNotDeleted(),
                        member.id.ne(loginMemberId))    // 내가 작성한 리뷰는 보이지 않아야 함
                .orderBy(matchFavoriteFoodCategories(favoriteFoodCategories),
                        review.createdAt.desc())
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize() + 1)
                .fetch();

        List<ReviewDto> content = tuples.stream()
                .map(tuple -> ReviewDto.from(
                        Objects.requireNonNull(tuple.get(review)),
                        List.of(WRITER, PLACE),
                        Boolean.TRUE.equals(tuple.get(isMarkedPlace(loginMemberId)))
                )).collect(Collectors.toList());

        boolean hasNext = false;
        if (content.size() > pageable.getPageSize()) {
            content.remove(pageable.getPageSize());
            hasNext = true;
        }

        return new SliceImpl<>(
                content,
                PageRequest.of(
                        pageable.getPageNumber(),
                        pageable.getPageSize(),
                        Sort.by(Sort.Order.asc("isFavoriteFoodCategoryOfReview"),
                                Sort.Order.desc("createdAt"))
                ),
                hasNext
        );
    }

    /**
     * 장소의 firstCategory가 회원의 선호 음식 카테고리(favoriteFoodCategories)에 해당할 때 더 높은 우선순위를 갖도록 설정한다.
     *
     * @param favoriteFoodCategories 회원의 선호 음식 카테고리 목록
     * @return 정렬 정보(OrderSpecifier)
     */
    private OrderSpecifier<Integer> matchFavoriteFoodCategories(List<FavoriteFoodCategory> favoriteFoodCategories) {
        List<String> favoritePlaceFirstCategories = favoriteFoodCategories.stream()
                .flatMap(foodCategory -> foodCategory.getCategory().getMatchingFirstCategories().stream())
                .toList();
        return new CaseBuilder()
                .when(place.category.firstCategory.in(favoritePlaceFirstCategories)).then(1)
                .otherwise(2)
                .asc();
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

    private BooleanExpression writerEqualFilteringCondition(Long writerId) {
        return writerId != null ? member.id.eq(writerId) : null;
    }

    private BooleanExpression placeEqualFilteringCondition(Long placeId) {
        return placeId != null ? place.id.eq(placeId) : null;
    }
}
