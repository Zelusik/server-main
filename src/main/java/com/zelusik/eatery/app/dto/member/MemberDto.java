package com.zelusik.eatery.app.dto.member;

import com.zelusik.eatery.app.domain.Member;
import com.zelusik.eatery.app.constant.member.Gender;
import com.zelusik.eatery.app.constant.member.LoginType;
import com.zelusik.eatery.app.dto.terms_info.TermsInfoDto;

import java.time.LocalDateTime;

public record MemberDto(
        Long id,
        TermsInfoDto termsInfoDto,
        String socialUid,
        LoginType loginType,
        String email,
        String nickname,
        Integer ageRange,
        Gender gender,
        LocalDateTime createdAt,
        LocalDateTime updatedAt,
        LocalDateTime deletedAt
) {
    public static MemberDto of(String socialUid, LoginType loginType, String email, String nickname, Integer ageRange, Gender gender) {
        return of(null, null, socialUid, loginType, email, nickname, ageRange, gender, null, null, null);
    }

    public static MemberDto of(Long id, TermsInfoDto termsInfoDto, String socialUid, LoginType loginType, String email, String nickname, Integer ageRange, Gender gender, LocalDateTime createdAt, LocalDateTime updatedAt, LocalDateTime deletedAt) {
        return new MemberDto(id, termsInfoDto, socialUid, loginType, email, nickname, ageRange, gender, createdAt, updatedAt, deletedAt);
    }

    public static MemberDto from(Member entity) {
        return of(
                entity.getId(),
                TermsInfoDto.from(entity.getTermsInfo()),
                entity.getSocialUid(),
                entity.getLoginType(),
                entity.getEmail(),
                entity.getNickname(),
                entity.getAgeRange(),
                entity.getGender(),
                entity.getCreatedAt(),
                entity.getUpdatedAt(),
                entity.getDeletedAt()
        );
    }

    public Member toEntity() {
        return Member.of(
                null,
                socialUid(),
                loginType(),
                email(),
                nickname(),
                ageRange(),
                gender()
        );
    }
}
