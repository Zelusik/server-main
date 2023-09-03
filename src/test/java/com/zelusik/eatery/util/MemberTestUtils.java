package com.zelusik.eatery.util;

import com.zelusik.eatery.constant.ConstantUtil;
import com.zelusik.eatery.constant.FoodCategoryValue;
import com.zelusik.eatery.constant.member.Gender;
import com.zelusik.eatery.constant.member.LoginType;
import com.zelusik.eatery.constant.member.RoleType;
import com.zelusik.eatery.domain.member.Member;
import com.zelusik.eatery.domain.member.ProfileImage;
import com.zelusik.eatery.dto.member.MemberDto;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

public class MemberTestUtils {

    public static final String SOCIAL_UID = "1234567890";
    public static final String EMAIL = "test@test.com";
    public static final String NICKNAME = "test";
    public static final Integer AGE_RANGE = 20;
    public static final Gender GENDER = Gender.MALE;

    public static MemberDto createMemberDto() {
        return createMemberDto(1L);
    }

    public static MemberDto createMemberDto(Long memberId) {
        return createMemberDto(memberId, Set.of(RoleType.USER));
    }

    public static MemberDto createMemberDto(Long memberId, Set<RoleType> roleTypes) {
        return new MemberDto(
                memberId,
                ConstantUtil.defaultProfileImageUrl,
                ConstantUtil.defaultProfileThumbnailImageUrl,
                SOCIAL_UID,
                LoginType.KAKAO,
                roleTypes,
                EMAIL,
                NICKNAME,
                LocalDate.of(1998, 1, 5),
                AGE_RANGE,
                GENDER,
                List.of(FoodCategoryValue.KOREAN),
                null
        );
    }

    public static Member createNotSavedMember(String socialId, String nickname) {
        return Member.of(
                null,
                "https://default-profile-image",
                "https://defualt-profile-thumbnail-image",
                socialId,
                LoginType.KAKAO,
                Set.of(RoleType.USER),
                "test@test.com" + socialId,
                nickname,
                null,
                null,
                null,
                null,
                null,
                null
        );
    }

    public static Member createMember(Long memberId) {
        return createMember(memberId, null);
    }

    public static Member createMember(Long memberId, LocalDateTime deletedAt) {
        return createMember(memberId, Set.of(RoleType.USER), deletedAt);
    }

    public static Member createMember(Long memberId, Set<RoleType> roleTypes, LocalDateTime deletedAt) {
        return Member.of(
                memberId,
                ConstantUtil.defaultProfileImageUrl,
                ConstantUtil.defaultProfileThumbnailImageUrl,
                SOCIAL_UID,
                LoginType.KAKAO,
                roleTypes,
                EMAIL,
                NICKNAME,
                LocalDate.of(1998, 1, 5),
                AGE_RANGE,
                GENDER,
                LocalDateTime.now(),
                LocalDateTime.now(),
                deletedAt
        );
    }

    public static ProfileImage createProfileImage(Long profileImageId) {
        return createProfileImage(createMember(1L), profileImageId);
    }

    public static ProfileImage createProfileImage(Member member, Long profileImageId) {
        return ProfileImage.of(
                profileImageId,
                member,
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
}
