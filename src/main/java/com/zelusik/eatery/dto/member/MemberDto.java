package com.zelusik.eatery.dto.member;

import com.zelusik.eatery.constant.FoodCategoryValue;
import com.zelusik.eatery.constant.member.Gender;
import com.zelusik.eatery.constant.member.LoginType;
import com.zelusik.eatery.constant.member.RoleType;
import com.zelusik.eatery.domain.member.FavoriteFoodCategory;
import com.zelusik.eatery.domain.member.Member;
import com.zelusik.eatery.dto.terms_info.TermsInfoDto;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Getter
public class MemberDto {

    private Long id;
    private TermsInfoDto termsInfoDto;
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

    public static MemberDto of(String profileImageUrl, String profileThumbnailImageUrl, String socialUid, LoginType loginType, Set<RoleType> roleTypes, String email, String nickname, Integer ageRange, Gender gender) {
        return of(null, null, profileImageUrl, profileThumbnailImageUrl, socialUid, loginType, roleTypes, email, nickname, null, ageRange, gender, null, null);
    }

    public static MemberDto of(Long id, TermsInfoDto termsInfoDto, String profileImageUrl, String profileThumbnailImageUrl, String socialUid, LoginType loginType, Set<RoleType> roleTypes, String email, String nickname, LocalDate birthDay, Integer ageRange, Gender gender, List<FoodCategoryValue> favoriteFoodCategories, LocalDateTime deletedAt) {
        return new MemberDto(id, termsInfoDto, profileImageUrl, profileThumbnailImageUrl, socialUid, loginType, roleTypes, email, nickname, birthDay, ageRange, gender, favoriteFoodCategories, deletedAt);
    }

    public static MemberDto from(Member entity) {
        return of(
                entity.getId(),
                TermsInfoDto.from(entity.getTermsInfo()),
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
