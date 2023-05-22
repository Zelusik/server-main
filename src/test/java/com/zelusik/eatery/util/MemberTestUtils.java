package com.zelusik.eatery.util;

import com.zelusik.eatery.constant.ConstantUtil;
import com.zelusik.eatery.constant.FoodCategory;
import com.zelusik.eatery.constant.member.Gender;
import com.zelusik.eatery.constant.member.LoginType;
import com.zelusik.eatery.constant.review.MemberDeletionSurveyType;
import com.zelusik.eatery.domain.member.*;
import com.zelusik.eatery.dto.member.MemberDeletionSurveyDto;
import com.zelusik.eatery.dto.member.MemberDto;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

public class MemberTestUtils {

    public static final String SOCIAL_UID = "1234567890";
    public static final String EMAIL = "test@test.com";
    public static final String NICKNAME = "test";
    public static final Integer AGE_RANGE = 20;
    public static final Gender GENDER = Gender.MALE;

    public static MemberDto createMemberDto() {
        return MemberDto.of(
                ConstantUtil.defaultProfileImageUrl,
                ConstantUtil.defaultProfileThumbnailImageUrl,
                SOCIAL_UID,
                LoginType.KAKAO,
                EMAIL,
                NICKNAME,
                AGE_RANGE,
                GENDER
        );
    }

    public static MemberDto createMemberDtoWithId() {
        return createMemberDtoWithId(1L);
    }

    public static MemberDto createMemberDtoWithId(Long memberId) {
        return MemberDto.of(
                1L,
                null,
                ConstantUtil.defaultProfileImageUrl,
                ConstantUtil.defaultProfileThumbnailImageUrl,
                SOCIAL_UID,
                LoginType.KAKAO,
                EMAIL,
                NICKNAME,
                LocalDate.of(1998, 1, 5),
                AGE_RANGE,
                GENDER,
                List.of(FoodCategory.KOREAN),
                null,
                null,
                null
        );
    }

    public static Member createNotSavedMember() {
        return createMemberDto().toEntity();
    }

    public static Member createMember(Long memberId) {
        return createMember(memberId, null, null);
    }

    public static Member createMember(Long memberId, TermsInfo termsInfo, LocalDateTime deletedAt) {
        Member member = Member.of(
                memberId,
                termsInfo,
                ConstantUtil.defaultProfileImageUrl,
                ConstantUtil.defaultProfileThumbnailImageUrl,
                SOCIAL_UID,
                LoginType.KAKAO,
                EMAIL,
                NICKNAME,
                LocalDate.of(1998, 1, 5),
                AGE_RANGE,
                GENDER,
                LocalDateTime.now(),
                LocalDateTime.now(),
                deletedAt
        );

        List<FavoriteFoodCategory> favoriteFoodCategories = List.of(
                createFavoriteFoodCategory(100L, member, FoodCategory.KOREAN),
                createFavoriteFoodCategory(101L, member, FoodCategory.WESTERN),
                createFavoriteFoodCategory(102L, member, FoodCategory.BAR)
        );
        member.getFavoriteFoodCategories().addAll(favoriteFoodCategories);

        return member;
    }

    public static Member createDeletedMember(Long memberId) {
        return createMember(memberId, null, LocalDateTime.now());
    }

    public static Member createMemberWithTermsInfo(Long memberId) {
        return createMember(
                memberId,
                TermsInfo.of(
                        1L,
                        true,
                        true, LocalDateTime.of(2023, 1, 1, 0, 0),
                        true, LocalDateTime.of(2023, 1, 1, 0, 0),
                        true, LocalDateTime.of(2023, 1, 1, 0, 0),
                        true, LocalDateTime.of(2023, 1, 1, 0, 0),
                        LocalDateTime.of(2023, 1, 1, 0, 0),
                        LocalDateTime.of(2023, 1, 1, 0, 0)
                ),
                null
        );
    }

    public static ProfileImage createNotSavedProfileImage(Member member) {
        return ProfileImage.of(
                member,
                "originalFilename",
                "storedFilename",
                "url",
                "thumbnailStoredFilename",
                "thumbnailUrl"
        );
    }

    public static ProfileImage createProfileImage(Long profileImageId) {
        return createProfileImage(createMember(1L),  profileImageId);
    }

    public static ProfileImage createProfileImage(Member member, Long profileImageId) {
        return ProfileImage.of(
                profileImageId,
                createMember(1L),
                "originalFilename",
                "storedFilename",
                "url",
                "thumbnailStoredFilename",
                "thumbnailUrl",
                LocalDateTime.of(2023, 1, 1, 0, 0),
                LocalDateTime.of(2023, 1, 1, 0, 0),
                null
        );
    }

    public static MemberDeletionSurveyDto createMemberDeletionSurveyDto(Long memberId, MemberDeletionSurveyType surveyType) {
        return MemberDeletionSurveyDto.of(
                10L,
                memberId,
                surveyType
        );
    }

    public static MemberDeletionSurvey createMemberDeletionSurvey(Member member, MemberDeletionSurveyType surveyType) {
        return MemberDeletionSurvey.of(
                10L,
                member,
                surveyType,
                LocalDateTime.of(2023, 1, 1, 0, 0),
                LocalDateTime.of(2023, 1, 1, 0, 0)
        );
    }

    public static FavoriteFoodCategory createFavoriteFoodCategory(Long id, Member member, FoodCategory foodCategory) {
        return FavoriteFoodCategory.of(id, member, foodCategory);
    }
}
