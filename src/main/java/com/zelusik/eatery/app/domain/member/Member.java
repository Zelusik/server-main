package com.zelusik.eatery.app.domain.member;

import com.zelusik.eatery.app.constant.FoodCategory;
import com.zelusik.eatery.app.constant.member.Gender;
import com.zelusik.eatery.app.constant.member.LoginType;
import com.zelusik.eatery.app.domain.BaseTimeEntity;
import com.zelusik.eatery.app.domain.TermsInfo;
import com.zelusik.eatery.app.util.domain.FoodCategoryConverter;
import lombok.*;
import org.hibernate.annotations.SQLDelete;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.List;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@SQLDelete(sql = "UPDATE member SET deleted_at = CURRENT_TIMESTAMP WHERE member_id = ?")
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

    @Column(nullable = false)
    private String profileImageUrl;

    @Column(nullable = false)
    private String profileThumbnailImageUrl;

    @Column(nullable = false, unique = true)
    private String socialUid;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private LoginType loginType;

    @Column(unique = true)
    private String email;

    @Column(nullable = false)
    private String nickname;

    private Integer ageRange;

    @Enumerated(EnumType.STRING)
    private Gender gender;

    @Setter
    @Convert(converter = FoodCategoryConverter.class)
    private List<FoodCategory> favoriteFoodCategories;

    private LocalDateTime deletedAt;

    public static Member of(String profileImageUrl, String profileThumbnailImageUrl, String socialUid, LoginType loginType, String email, String nickname, Integer ageRange, Gender gender) {
        return of(null, null, profileImageUrl, profileThumbnailImageUrl, socialUid, loginType, email, nickname, ageRange, gender, null, null, null, null);
    }

    public static Member of(Long id, TermsInfo termsInfo, String profileImageUrl, String profileThumbnailImageUrl, String socialUid, LoginType loginType, String email, String nickname, Integer ageRange, Gender gender, List<FoodCategory> favoriteFoodCategories, LocalDateTime deletedAt, LocalDateTime createdAt, LocalDateTime updatedAt) {
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
                .gender(gender)
                .favoriteFoodCategories(favoriteFoodCategories)
                .createdAt(createdAt)
                .updatedAt(updatedAt)
                .deletedAt(deletedAt)
                .build();
    }

    @Builder(access = AccessLevel.PRIVATE)
    private Member(Long id, TermsInfo termsInfo, String profileImageUrl, String profileThumbnailImageUrl, String socialUid, LoginType loginType, String email, String nickname, Integer ageRange, Gender gender, List<FoodCategory> favoriteFoodCategories, LocalDateTime deletedAt, LocalDateTime createdAt, LocalDateTime updatedAt) {
        super(createdAt, updatedAt);
        this.id = id;
        this.termsInfo = termsInfo;
        this.profileImageUrl = profileImageUrl;
        this.profileThumbnailImageUrl = profileThumbnailImageUrl;
        this.socialUid = socialUid;
        this.loginType = loginType;
        this.email = email;
        this.nickname = nickname;
        this.ageRange = ageRange;
        this.gender = gender;
        this.favoriteFoodCategories = favoriteFoodCategories;
        this.deletedAt = deletedAt;
    }
}