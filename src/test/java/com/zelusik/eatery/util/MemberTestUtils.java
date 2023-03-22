package com.zelusik.eatery.util;

import com.zelusik.eatery.app.constant.ConstantUtil;
import com.zelusik.eatery.app.constant.FoodCategory;
import com.zelusik.eatery.app.domain.member.Member;
import com.zelusik.eatery.app.constant.member.Gender;
import com.zelusik.eatery.app.constant.member.LoginType;
import com.zelusik.eatery.app.dto.member.MemberDto;

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

    public static Member createMember(Long memberId) {
        return Member.of(
                memberId,
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
                LocalDateTime.now(),
                LocalDateTime.now(),
                null
        );
    }
}
