package com.zelusik.eatery.app.dto.member;

import com.zelusik.eatery.app.constant.FoodCategory;
import com.zelusik.eatery.app.constant.member.Gender;
import com.zelusik.eatery.app.constant.member.LoginType;
import com.zelusik.eatery.app.domain.member.Member;
import com.zelusik.eatery.app.dto.terms_info.TermsInfoDto;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Getter
public class MemberDto {

    private Long id;
    private TermsInfoDto termsInfoDto;
    private String profileImageUrl;
    private String profileThumbnailImageUrl;
    private String socialUid;
    private LoginType loginType;
    private String email;
    private String nickname;
    private LocalDate birthDay;
    private Integer ageRange;
    private Gender gender;
    private List<FoodCategory> favoriteFoodCategories;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private LocalDateTime deletedAt;

    public static MemberDto of(String profileImageUrl, String profileThumbnailImageUrl, String socialUid, LoginType loginType, String email, String nickname, Integer ageRange, Gender gender) {
        return of(null, null, profileImageUrl, profileThumbnailImageUrl, socialUid, loginType, email, nickname, null, ageRange, gender, null, null, null, null);
    }

    public static MemberDto of(Long id, TermsInfoDto termsInfoDto, String profileImageUrl, String profileThumbnailImageUrl, String socialUid, LoginType loginType, String email, String nickname, LocalDate birthDay, Integer ageRange, Gender gender, List<FoodCategory> favoriteFoodCategories, LocalDateTime createdAt, LocalDateTime updatedAt, LocalDateTime deletedAt) {
        return new MemberDto(id, termsInfoDto, profileImageUrl, profileThumbnailImageUrl, socialUid, loginType, email, nickname, birthDay, ageRange, gender, favoriteFoodCategories, createdAt, updatedAt, deletedAt);
    }

    public static MemberDto from(Member entity) {
        return of(
                entity.getId(),
                TermsInfoDto.from(entity.getTermsInfo()),
                entity.getProfileImageUrl(),
                entity.getProfileThumbnailImageUrl(),
                entity.getSocialUid(),
                entity.getLoginType(),
                entity.getEmail(),
                entity.getNickname(),
                entity.getBirthDay(),
                entity.getAgeRange(),
                entity.getGender(),
                entity.getFavoriteFoodCategories(),
                entity.getCreatedAt(),
                entity.getUpdatedAt(),
                entity.getDeletedAt()
        );
    }

    public Member toEntity() {
        return Member.of(
                this.getProfileImageUrl(),
                this.getProfileThumbnailImageUrl(),
                this.getSocialUid(),
                this.getLoginType(),
                this.getEmail(),
                this.getNickname(),
                this.getAgeRange(),
                this.getGender()
        );
    }
}
