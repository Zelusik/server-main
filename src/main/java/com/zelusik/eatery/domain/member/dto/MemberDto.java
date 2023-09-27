package com.zelusik.eatery.domain.member.dto;

import com.zelusik.eatery.global.common.constant.FoodCategoryValue;
import com.zelusik.eatery.domain.member.constant.Gender;
import com.zelusik.eatery.domain.member.constant.LoginType;
import com.zelusik.eatery.domain.member.constant.RoleType;
import com.zelusik.eatery.domain.favorite_food_category.entity.FavoriteFoodCategory;
import com.zelusik.eatery.domain.member.entity.Member;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PRIVATE)
@Getter
public class MemberDto {

    private Long id;
    private String profileImageUrl;
    private String profileThumbnailImageUrl;
    private String socialUid;
    private LoginType loginType;
    private Set<RoleType> roleTypes;
    private String email;
    private String nickname;
    private LocalDate birthDay;
    private Integer ageRange;
    private Gender gender;
    private List<FoodCategoryValue> favoriteFoodCategories;
    private LocalDateTime deletedAt;

    public MemberDto(String profileImageUrl, String profileThumbnailImageUrl, String socialUid, LoginType loginType, Set<RoleType> roleTypes, String email, String nickname, Integer ageRange, Gender gender) {
        this(null, profileImageUrl, profileThumbnailImageUrl, socialUid, loginType, roleTypes, email, nickname, null, ageRange, gender, null, null);
    }

    public static MemberDto from(Member entity) {
        return new MemberDto(
                entity.getId(),
                entity.getProfileImageUrl(),
                entity.getProfileThumbnailImageUrl(),
                entity.getSocialUid(),
                entity.getLoginType(),
                entity.getRoleTypes(),
                entity.getEmail(),
                entity.getNickname(),
                entity.getBirthDay(),
                entity.getAgeRange(),
                entity.getGender(),
                entity.getFavoriteFoodCategories().stream()
                        .map(FavoriteFoodCategory::getCategory)
                        .toList(),
                entity.getDeletedAt()
        );
    }

    public Member toEntity() {
        return Member.of(
                this.getProfileImageUrl(),
                this.getProfileThumbnailImageUrl(),
                this.getSocialUid(),
                this.getLoginType(),
                this.getRoleTypes(),
                this.getEmail(),
                this.getNickname(),
                this.getAgeRange(),
                this.getGender()
        );
    }
}
