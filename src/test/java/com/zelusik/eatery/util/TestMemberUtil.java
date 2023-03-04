package com.zelusik.eatery.util;

import com.zelusik.eatery.app.domain.Member;
import com.zelusik.eatery.app.domain.constant.Gender;
import com.zelusik.eatery.app.domain.constant.LoginType;
import com.zelusik.eatery.app.dto.member.MemberDto;
import org.springframework.test.util.ReflectionTestUtils;

public class TestMemberUtil {

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

    public static Member createMember() {
        return createMemberDto().toEntity();
    }

    public static Member createMemberWithId() {
        Member member = createMember();
        ReflectionTestUtils.setField(member, "id", 1L);

        return member;
    }
}
