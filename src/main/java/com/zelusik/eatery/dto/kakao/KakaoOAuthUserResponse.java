package com.zelusik.eatery.dto.kakao;

import com.zelusik.eatery.constant.ConstantUtil;
import com.zelusik.eatery.constant.member.Gender;
import com.zelusik.eatery.constant.member.LoginType;
import com.zelusik.eatery.dto.member.MemberDto;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Map;

@SuppressWarnings("unchecked")  // TODO: Map -> Object 변환 로직이 있어서 generic type casting 문제를 무시한다. 더 좋은 방법이 있다면 고려할 수 있음.
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Getter
public class KakaoOAuthUserResponse {

    private String id;
    private LocalDateTime connectedAt;
    private Map<String, Object> properties;
    private KakaoAccount kakaoAccount;

    @AllArgsConstructor(access = AccessLevel.PRIVATE)
    @Getter
    public static class KakaoAccount {

        private Boolean profileNicknameNeedsAgreement;
        private Boolean profileImageNeedsAgreement;
        private Profile profile;
        private Boolean hasEmail;
        private Boolean emailNeedsAgreement;
        private Boolean isEmailValid;
        private Boolean isEmailVerified;
        private String email;
        private Boolean hasAgeRange;
        private Boolean ageRangeNeedsAgreement;
        private Integer ageRange;
        private Boolean hasGender;
        private Boolean genderNeedsAgreement;
        private Gender gender;

        @AllArgsConstructor(access = AccessLevel.PRIVATE)
        @Getter
        public static class Profile {

            private String nickname;
            private String thumbnailImageUrl;
            private String profileImageUrl;

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

    public static KakaoOAuthUserResponse from(Map<String, Object> attributes) {
        return new KakaoOAuthUserResponse(
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
                getGender() != null ? getGender() : Gender.ETC
        );
    }

    // Getter
    public String getSocialUid() {
        return this.getId();
    }

    public String getNickname() {
        return this.getKakaoAccount().getProfile().getNickname();
    }

    public String getThumbnailImageUrl() {
        return this.getKakaoAccount().getProfile().getThumbnailImageUrl();
    }

    public String getProfileImageUrl() {
        return this.getKakaoAccount().getProfile().getProfileImageUrl();
    }

    public String getEmail() {
        return this.getKakaoAccount().getEmail();
    }

    public Integer getAgeRange() {
        return this.getKakaoAccount().getAgeRange();
    }

    public Gender getGender() {
        return this.getKakaoAccount().getGender();
    }
}
