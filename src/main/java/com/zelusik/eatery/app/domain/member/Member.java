package com.zelusik.eatery.app.domain.member;

import com.zelusik.eatery.app.constant.FoodCategory;
import com.zelusik.eatery.app.constant.member.Gender;
import com.zelusik.eatery.app.constant.member.LoginType;
import com.zelusik.eatery.app.domain.BaseTimeEntity;
import com.zelusik.eatery.app.util.domain.FoodCategoryConverter;
import lombok.*;
import org.hibernate.annotations.SQLDelete;

import javax.persistence.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@SQLDelete(sql = "UPDATE member SET deleted_at = CURRENT_TIMESTAMP WHERE member_id = ?")
@Table(indexes = {
        @Index(columnList = "socialUid")
})
@Entity
public class Member extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "member_id", nullable = false)
    private Long id;

    @Setter
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
    @Convert(converter = FoodCategoryConverter.class)
    private List<FoodCategory> favoriteFoodCategories;

    @Setter(AccessLevel.PRIVATE)
    private LocalDateTime deletedAt;

    public static Member of(String profileImageUrl, String profileThumbnailImageUrl, String socialUid, LoginType loginType, String email, String nickname, Integer ageRange, Gender gender) {
        return of(null, null, profileImageUrl, profileThumbnailImageUrl, socialUid, loginType, email, nickname, null, ageRange, gender, null, null, null, null);
    }

    public static Member of(Long id, TermsInfo termsInfo, String profileImageUrl, String profileThumbnailImageUrl, String socialUid, LoginType loginType, String email, String nickname, LocalDate birthDay, Integer ageRange, Gender gender, List<FoodCategory> favoriteFoodCategories, LocalDateTime deletedAt, LocalDateTime createdAt, LocalDateTime updatedAt) {
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
