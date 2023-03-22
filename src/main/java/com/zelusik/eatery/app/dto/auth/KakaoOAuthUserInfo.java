package com.zelusik.eatery.app.dto.auth;

import com.zelusik.eatery.app.constant.ConstantUtil;
import com.zelusik.eatery.app.constant.member.Gender;
import com.zelusik.eatery.app.constant.member.LoginType;
import com.zelusik.eatery.app.dto.member.MemberDto;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Map;

@SuppressWarnings("unchecked")  // TODO: Map -> Object 변환 로직이 있어서 generic type casting 문제를 무시한다. 더 좋은 방법이 있다면 고려할 수 있음.
public record KakaoOAuthUserInfo(
        String id,
        LocalDateTime connectedAt,
        Map<String, Object> properties,
        KakaoAccount kakaoAccount
) {
    public record KakaoAccount(
            Boolean profileNicknameNeedsAgreement,
            Boolean profileImageNeedsAgreement,
            Profile profile,
            Boolean hasEmail,
            Boolean emailNeedsAgreement,
            Boolean isEmailValid,
            Boolean isEmailVerified,
            String email,
            Boolean hasAgeRange,
            Boolean ageRangeNeedsAgreement,
            Integer ageRange,
            Boolean hasGender,
            Boolean genderNeedsAgreement,
            Gender gender
    ) {
        public record Profile(
                String nickname,
                String thumbnailImageUrl,
                String profileImageUrl
        ) {
            public static Profile from(Map<String, Object> attributes) {
                Object thumbnailImageUrl = attributes.get("thumbnail_image_url");
                Object profileImageUrl = attributes.get("profile_image_url");

                return new Profile(
                        attributes.get("nickname").toString(),
                        thumbnailImageUrl == null ? null : thumbnailImageUrl.toString(),
                        profileImageUrl == null ? null : profileImageUrl.toString()
                );
            }
        }

        public static KakaoAccount from(Map<String, Object> attributes) {
            Object email = attributes.get("email");
            Object ageRange = attributes.get("age_range");
            Object gender = attributes.get("gender");

            return new KakaoAccount(
                    Boolean.valueOf(String.valueOf(attributes.get("profile_nickname_needs_agreement"))),
                    Boolean.valueOf(String.valueOf(attributes.get("profile_image_needs_agreement"))),
                    Profile.from((Map<String, Object>) attributes.get("profile")),
                    Boolean.valueOf(String.valueOf(attributes.get("has_email"))),
                    Boolean.valueOf(String.valueOf(attributes.get("email_needs_agreement"))),
                    Boolean.valueOf(String.valueOf(attributes.get("is_email_valid"))),
                    Boolean.valueOf(String.valueOf(attributes.get("is_email_verified"))),
                    email == null ? null : email.toString(),
                    Boolean.valueOf(String.valueOf(attributes.get("has_age_range"))),
                    Boolean.valueOf(String.valueOf(attributes.get("age_range_needs_agreement"))),
                    ageRange == null ? null : Integer.valueOf(ageRange.toString().substring(0, 2)),
                    Boolean.valueOf(String.valueOf(attributes.get("has_gender"))),
                    Boolean.valueOf(String.valueOf(attributes.get("gender_needs_agreement"))),
                    gender == null ? null : Gender.caseFreeValueOf(gender.toString())
            );
        }
    }

    public static KakaoOAuthUserInfo from(Map<String, Object> attributes) {
        return new KakaoOAuthUserInfo(
                String.valueOf(attributes.get("id")),
                ZonedDateTime.parse(
                        String.valueOf(attributes.get("connected_at")),
                        DateTimeFormatter.ISO_INSTANT.withZone(ZoneId.systemDefault())
                ).toLocalDateTime(),
                (Map<String, Object>) attributes.get("properties"),
                KakaoAccount.from((Map<String, Object>) attributes.get("kakao_account"))
        );
    }

    public MemberDto toMemberDto() {
        String profileImageUrl = getProfileImageUrl();
        if (profileImageUrl == null) {
            profileImageUrl = ConstantUtil.defaultProfileImageUrl;
        }

        String thumbnailImageUrl = getThumbnailImageUrl();
        if (thumbnailImageUrl == null) {
            thumbnailImageUrl = ConstantUtil.defaultProfileThumbnailImageUrl;
        }

        return MemberDto.of(
                profileImageUrl,
                thumbnailImageUrl,
                getSocialUid(),
                LoginType.KAKAO,
                getEmail(),
                getNickname(),
                getAgeRange(),
                getGender()
        );
    }

    // Getter
    public String getSocialUid() {
        return this.id();
    }

    public String getNickname() {
        return this.kakaoAccount().profile().nickname();
    }

    public String getThumbnailImageUrl() {
        return this.kakaoAccount().profile().thumbnailImageUrl();
    }

    public String getProfileImageUrl() {
        return this.kakaoAccount().profile().profileImageUrl();
    }

    public String getEmail() {
        return this.kakaoAccount().email();
    }

    public Integer getAgeRange() {
        return this.kakaoAccount().ageRange();
    }

    public Gender getGender() {
        return this.kakaoAccount().gender();
    }
}
