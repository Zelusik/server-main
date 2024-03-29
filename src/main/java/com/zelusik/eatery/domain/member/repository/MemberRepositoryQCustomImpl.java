package com.zelusik.eatery.domain.member.repository;

import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.StringExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.zelusik.eatery.domain.member.dto.MemberWithProfileInfoDto;
import com.zelusik.eatery.domain.member.entity.Member;
import com.zelusik.eatery.domain.member.entity.QMember;
import com.zelusik.eatery.domain.member.exception.MemberNotFoundByIdException;
import com.zelusik.eatery.domain.review.constant.ReviewKeywordValue;
import com.zelusik.eatery.global.common.constant.FoodCategoryValue;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.SliceImpl;
import org.springframework.lang.Nullable;

import java.util.List;
import java.util.Optional;

import static com.zelusik.eatery.domain.member.entity.QMember.member;
import static com.zelusik.eatery.domain.place.entity.QPlace.place;
import static com.zelusik.eatery.domain.review.entity.QReview.review;
import static com.zelusik.eatery.domain.review_keyword.entity.QReviewKeyword.reviewKeyword;

@RequiredArgsConstructor
public class MemberRepositoryQCustomImpl implements MemberRepositoryQCustom {

    private final JPAQueryFactory queryFactory;

    @Override
    public MemberWithProfileInfoDto getMemberProfileInfoById(long memberId) {
        Member member = Optional.ofNullable(
                queryFactory
                        .select(QMember.member)
                        .from(QMember.member)
                        .where(isMemberNotDeleted(), QMember.member.id.eq(memberId))
                        .fetchOne()
        ).orElseThrow(() -> new MemberNotFoundByIdException(memberId));

        return MemberWithProfileInfoDto.from(
                member,
                getNumOfReviews(memberId),
                getMostVisitedLocation(memberId),
                getMostTaggedReviewKeyword(memberId),
                getMostEatenFoodCategory(memberId)
        );
    }

    private BooleanExpression isReviewNotDeleted() {
        return review.deletedAt.isNull();
    }

    /**
     * 작성한 리뷰 수를 조회한다.
     *
     * @param memberId 작성한 리뷰 수를 조회할 회원의 PK
     * @return 회원이 작성한 리뷰 수
     */
    private Integer getNumOfReviews(long memberId) {
        return queryFactory
                .select(review.count().intValue())
                .from(review)
                .where(isReviewNotDeleted(), review.writer.id.eq(memberId))
                .fetchOne();
    }

    /**
     * <p>가장 많이 방문한 장소.
     * <p>작성한 리뷰 중 가장 많이 겹치는 읍면동 주소가 조회된다.
     *
     * @param memberId 가장 많이 방문한 장소를 조회할 회원의 PK
     * @return 회원잉 가장 많이 방문한 장소 정보. 읍면동 단위의 주소 정보로 반환된다.
     */
    private String getMostVisitedLocation(long memberId) {
        String result = queryFactory
                .select(extractEmdAddress())
                .from(review)
                .where(isReviewNotDeleted(), review.writer.id.eq(memberId))
                .groupBy(extractEmdAddress())
                .orderBy(extractEmdAddress().count().desc())
                .fetchFirst();
        return result != null ? result : "";
    }

    /**
     * 지번 주소에서 읍면동 단위의 주소 정보를 추출한다.
     *
     * @return 추출된 읍면동 단위 주소 정보. ex) 연남동, 이의동
     */
    private static StringExpression extractEmdAddress() {
        return review.place.address.lotNumberAddress.substring(0, review.place.address.lotNumberAddress.indexOf(" "));
    }

    @Nullable
    private ReviewKeywordValue getMostTaggedReviewKeyword(long memberId) {
        return queryFactory
                .select(reviewKeyword.keyword)
                .from(reviewKeyword)
                .join(reviewKeyword.review, review)
                .where(isReviewNotDeleted(), review.writer.id.eq(memberId))
                .groupBy(reviewKeyword.keyword)
                .orderBy(reviewKeyword.keyword.count().desc())
                .fetchFirst();
    }

    @Nullable
    private FoodCategoryValue getMostEatenFoodCategory(long memberId) {
        String result = queryFactory
                .select(place.category.firstCategory)
                .from(review)
                .join(review.place, place)
                .where(isReviewNotDeleted(), review.writer.id.eq(memberId))
                .groupBy(place.category.firstCategory)
                .orderBy(place.category.firstCategory.count().desc())
                .fetchFirst();
        return result != null ? FoodCategoryValue.valueOfFirstCategory(result) : null;
    }

    @Override
    public Slice<Member> searchByKeyword(String searchKeyword, Pageable pageable) {
        List<Member> content = queryFactory.selectFrom(member)
                .where(isMemberNotDeleted(), member.nickname.nickname.containsIgnoreCase(searchKeyword))
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize() + 1)
                .fetch();

        boolean hasNext = false;
        if (content.size() > pageable.getPageSize()) {
            content.remove(pageable.getPageSize());
            hasNext = true;
        }

        return new SliceImpl<>(content, pageable, hasNext);
    }

    @Override
    public boolean existsByNickname(String nickname) {
        Long count = queryFactory
                .select(member.count())
                .from(member)
                .where(member.nickname.nickname.eq(nickname))
                .fetchOne();
        return count != null && count >= 1;
    }

    private BooleanExpression isMemberNotDeleted() {
        return member.deletedAt.isNull();
    }
}
