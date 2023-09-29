package com.zelusik.eatery.domain.member.dto;

import com.zelusik.eatery.domain.member.entity.Member;
import com.zelusik.eatery.domain.review.constant.ReviewKeywordValue;
import com.zelusik.eatery.global.common.constant.FoodCategoryValue;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.lang.Nullable;

@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PRIVATE)
@Getter
public class MemberWithProfileInfoDto {

    private MemberDto member;

    private Integer numOfReviews;

    private Integer influence;

    private Integer numOfFollowers;

    private Integer numOfFollowings;

    private String mostVisitedLocation;

    @Nullable
    private ReviewKeywordValue mostTaggedReviewKeyword;

    @Nullable
    private FoodCategoryValue mostEatenFoodCategory;

    public static MemberWithProfileInfoDto of(Member member, int numOfReviews, String mostVisitedLocation, ReviewKeywordValue mostUsedReviewKeyword, FoodCategoryValue mostEatenFoodCategory) {
        return new MemberWithProfileInfoDto(
                MemberDto.from(member),
                numOfReviews,
                0,  // TODO: 영향력 관련 기능 구현 후 로직 수정 필요
                0,  // TODO: 팔로우 기능 구현 후 로직 수정 필요
                0,  // TODO: 팔로우 기능 구현 후 로직 수정 필요
                mostVisitedLocation,
                mostUsedReviewKeyword,
                mostEatenFoodCategory
        );
    }
}
