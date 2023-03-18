package com.zelusik.eatery.util;

import com.zelusik.eatery.app.constant.FoodCategory;
import com.zelusik.eatery.app.domain.Member;
import com.zelusik.eatery.app.constant.member.Gender;
import com.zelusik.eatery.app.constant.member.LoginType;
import com.zelusik.eatery.app.dto.member.MemberDto;
import org.springframework.test.util.ReflectionTestUtils;

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
                SOCIAL_UID,
                LoginType.KAKAO,
                EMAIL,
                NICKNAME,
                AGE_RANGE,
                GENDER
        );
    }

    public static MemberDto createMemberDtoWithId() {
        return MemberDto.of(
                1L,
                null,
                SOCIAL_UID,
                LoginType.KAKAO,
                EMAIL,
                NICKNAME,
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
                SOCIAL_UID,
                LoginType.KAKAO,
                EMAIL,
                NICKNAME,
                AGE_RANGE,
                GENDER,
                List.of(FoodCategory.KOREAN),
                LocalDateTime.now(),
                LocalDateTime.now(),
                null
        );
    }
}
