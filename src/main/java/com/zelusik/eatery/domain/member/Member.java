package com.zelusik.eatery.domain.member;

import com.zelusik.eatery.constant.FoodCategory;
import com.zelusik.eatery.constant.member.Gender;
import com.zelusik.eatery.constant.member.LoginType;
import com.zelusik.eatery.domain.BaseTimeEntity;
import lombok.*;

import javax.persistence.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@Table(indexes = {
        @Index(columnList = "socialUid")
})
@Entity
public class Member extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "member_id", nullable = false)
    private Long id;

    @Setter(AccessLevel.PRIVATE)
    @JoinColumn(name = "terms_info_id")
    @OneToOne(fetch = FetchType.LAZY)
    private TermsInfo termsInfo;

    @Setter(AccessLevel.PRIVATE)
    @Column(nullable = false)
    private String profileImageUrl;

    @Setter(AccessLevel.PRIVATE)
    @Column(nullable = false)
    private String profileThumbnailImageUrl;

    @Column(nullable = false, unique = true)
    private String socialUid;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private LoginType loginType;

    @Column(unique = true)
    private String email;

    @Setter(AccessLevel.PRIVATE)
    @Column(nullable = false, length = 15)
    private String nickname;

    @Setter(AccessLevel.PRIVATE)
    private LocalDate birthDay;

    private Integer ageRange;

    @Setter(AccessLevel.PRIVATE)
    @Enumerated(EnumType.STRING)
    private Gender gender;

    @Setter
    @CollectionTable(name = "member_favorite_food_gategory", joinColumns = @JoinColumn(name = "member_id"))
    @ElementCollection
    private List<FoodCategory> favoriteFoodCategories;

    @Setter(AccessLevel.PRIVATE)
    private LocalDateTime deletedAt;

    public static Member of(String profileImageUrl, String profileThumbnailImageUrl, String socialUid, LoginType loginType, String email, String nickname, Integer ageRange, Gender gender) {
        return of(null, null, profileImageUrl, profileThumbnailImageUrl, socialUid, loginType, email, nickname, null, ageRange, gender, null, null, null, null);
    }

    public static Member of(Long id, TermsInfo termsInfo, String profileImageUrl, String profileThumbnailImageUrl, String socialUid, LoginType loginType, String email, String nickname, LocalDate birthDay, Integer ageRange, Gender gender, List<FoodCategory> favoriteFoodCategories, LocalDateTime createdAt, LocalDateTime updatedAt, LocalDateTime deletedAt) {
        return Member.builder()
                .id(id)
                .termsInfo(termsInfo)
                .profileImageUrl(profileImageUrl)
                .profileThumbnailImageUrl(profileThumbnailImageUrl)
                .socialUid(socialUid)
                .loginType(loginType)
                .email(email)
                .nickname(nickname)
                .ageRange(ageRange)
                .birthDay(birthDay)
                .gender(gender)
                .favoriteFoodCategories(favoriteFoodCategories)
                .createdAt(createdAt)
                .updatedAt(updatedAt)
                .deletedAt(deletedAt)
                .build();
    }

    public void addTermsInfo(TermsInfo termsInfo) {
        this.setTermsInfo(termsInfo);
    }

    public void update(String profileImageUrl, String profileThumbnailImageUrl, String nickname, LocalDate birthDay, Gender gender) {
        this.setProfileImageUrl(profileImageUrl);
        this.setProfileThumbnailImageUrl(profileThumbnailImageUrl);
        this.setNickname(nickname);
        this.setBirthDay(birthDay);
        this.setGender(gender);
    }

    public void update(String nickname, LocalDate birthDay, Gender gender) {
        this.setNickname(nickname);
        this.setBirthDay(birthDay);
        this.setGender(gender);
    }

    public void rejoin() {
        this.setDeletedAt(null);
    }

    public void removeTermsInfo() {
        this.setTermsInfo(null);
    }

    public void softDelete() {
        this.setDeletedAt(LocalDateTime.now());
    }

    @Builder(access = AccessLevel.PRIVATE)
    private Member(Long id, TermsInfo termsInfo, String profileImageUrl, String profileThumbnailImageUrl, String socialUid, LoginType loginType, String email, String nickname, LocalDate birthDay, Integer ageRange, Gender gender, List<FoodCategory> favoriteFoodCategories, LocalDateTime deletedAt, LocalDateTime createdAt, LocalDateTime updatedAt) {
        super(createdAt, updatedAt);
        this.id = id;
        this.termsInfo = termsInfo;
        this.profileImageUrl = profileImageUrl;
        this.profileThumbnailImageUrl = profileThumbnailImageUrl;
        this.socialUid = socialUid;
        this.loginType = loginType;
        this.email = email;
        this.nickname = nickname;
        this.birthDay = birthDay;
        this.ageRange = ageRange;
        this.gender = gender;
        this.favoriteFoodCategories = favoriteFoodCategories;
        this.deletedAt = deletedAt;
    }
}
