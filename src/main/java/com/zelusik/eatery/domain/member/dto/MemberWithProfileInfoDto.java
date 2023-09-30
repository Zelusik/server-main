package com.zelusik.eatery.domain.member.dto;

import com.zelusik.eatery.domain.favorite_food_category.entity.FavoriteFoodCategory;
import com.zelusik.eatery.domain.member.constant.Gender;
import com.zelusik.eatery.domain.member.constant.LoginType;
import com.zelusik.eatery.domain.member.constant.RoleType;
import com.zelusik.eatery.domain.member.entity.Member;
import com.zelusik.eatery.domain.review.constant.ReviewKeywordValue;
import com.zelusik.eatery.global.common.constant.FoodCategoryValue;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
@Getter
public class MemberWithProfileInfoDto extends MemberDto {

    private Integer numOfReviews;
    private Integer influence;
    private Integer numOfFollowers;
    private Integer numOfFollowings;
    private String mostVisitedLocation;
    private ReviewKeywordValue mostTaggedReviewKeyword;
    private FoodCategoryValue mostEatenFoodCategory;

    public MemberWithProfileInfoDto(Long id, String profileImageUrl, String profileThumbnailImageUrl, String socialUid, LoginType loginType, Set<RoleType> roleTypes, String email, String nickname, LocalDate birthDay, Integer ageRange, Gender gender, List<FoodCategoryValue> favoriteFoodCategories, LocalDateTime deletedAt, Integer numOfReviews, String mostVisitedLocation, ReviewKeywordValue mostTaggedReviewKeyword, FoodCategoryValue mostEatenFoodCategory) {
        super(id, profileImageUrl, profileThumbnailImageUrl, socialUid, loginType, roleTypes, email, nickname, birthDay, ageRange, gender, favoriteFoodCategories, deletedAt);
        this.numOfReviews = numOfReviews;
        this.influence = 0; // TODO: 영향력 관련 기능 구현 후 로직 수정 필요
        this.numOfFollowers = 0;    // TODO: 팔로우 기능 구현 후 로직 수정 필요
        this.numOfFollowings = 0;   // TODO: 팔로우 기능 구현 후 로직 수정 필요
        this.mostVisitedLocation = mostVisitedLocation;
        this.mostTaggedReviewKeyword = mostTaggedReviewKeyword;
        this.mostEatenFoodCategory = mostEatenFoodCategory;
    }

    public static MemberWithProfileInfoDto from(Member member, Integer numOfReviews, String mostVisitedLocation, ReviewKeywordValue mostTaggedReviewKeyword, FoodCategoryValue mostEatenFoodCategory) {
        return new MemberWithProfileInfoDto(
                member.getId(),
                member.getProfileImageUrl(),
                member.getProfileThumbnailImageUrl(),
                member.getSocialUid(),
                member.getLoginType(),
                member.getRoleTypes(),
                member.getEmail(),
                member.getNickname(),
                member.getBirthDay(),
                member.getAgeRange(),
                member.getGender(),
                member.getFavoriteFoodCategories().stream()
                        .map(FavoriteFoodCategory::getCategory)
                        .toList(),
                member.getDeletedAt(),
                numOfReviews,
                mostVisitedLocation,
                mostTaggedReviewKeyword,
                mostEatenFoodCategory
        );
    }
}
