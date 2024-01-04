package com.zelusik.eatery.domain.member.entity;

import com.zelusik.eatery.domain.member.exception.InvalidNicknameException;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import javax.validation.constraints.NotBlank;
import java.util.Objects;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@Embeddable
public class MemberNickname {


    private static final int MEMBER_NICKNAME_MIN_LEN = 2;
    private static final int MEMBER_NICKNAME_MAX_LEN = 15;

    @NotBlank
    @Column(nullable = false, length = 15)
    String nickname;

    public MemberNickname(String nickname) {
        validateNickname(nickname);
        this.nickname = nickname;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o instanceof String that) {
            return this.getNickname().equals(that);
        }
        if (o instanceof MemberNickname that) {
            return this.getNickname().equals(that.getNickname());
        }
        return false;
    }

    @Override
    public int hashCode() {
        return Objects.hash(getNickname());
    }

    private static void validateNickname(String nickname) {
        int nicknameLength = nickname.length();
        if (nicknameLength < MEMBER_NICKNAME_MIN_LEN || nicknameLength > MEMBER_NICKNAME_MAX_LEN) {
            throw new InvalidNicknameException(String.format("%d글자 이상, %d글자 이하의 닉네임을 입력해주세요.", MEMBER_NICKNAME_MIN_LEN, MEMBER_NICKNAME_MAX_LEN));
        }
    }
}
