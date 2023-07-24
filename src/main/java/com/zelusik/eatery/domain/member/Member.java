package com.zelusik.eatery.domain.member;

import com.zelusik.eatery.constant.member.Gender;
import com.zelusik.eatery.constant.member.LoginType;
import com.zelusik.eatery.constant.member.RoleType;
import com.zelusik.eatery.converter.RoleTypesConverter;
import com.zelusik.eatery.domain.BaseTimeEntity;
import lombok.*;

import javax.persistence.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@Table(indexes = {
        @Index(columnList = "socialUid")
        @Index(name = "idx__member__created_at", columnList = "createdAt"),
        @Index(name = "idx__member__deleted_at", columnList = "deletedAt")
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

    @Column(nullable = false)
    @Convert(converter = RoleTypesConverter.class)
    private Set<RoleType> roleTypes;

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

    @OneToMany(mappedBy = "member")
    private List<FavoriteFoodCategory> favoriteFoodCategories = new LinkedList<>();

    @Setter(AccessLevel.PRIVATE)
    private LocalDateTime deletedAt;

    public static Member of(String profileImageUrl, String profileThumbnailImageUrl, String socialUid, LoginType loginType, Set<RoleType> roleTypes, String email, String nickname, Integer ageRange, Gender gender) {
        return of(null, null, profileImageUrl, profileThumbnailImageUrl, socialUid, loginType, roleTypes, email, nickname, null, ageRange, gender, null, null, null);
    }

    public static Member of(Long id, TermsInfo termsInfo, String profileImageUrl, String profileThumbnailImageUrl, String socialUid, LoginType loginType, Set<RoleType> roleTypes, String email, String nickname, LocalDate birthDay, Integer ageRange, Gender gender, LocalDateTime createdAt, LocalDateTime updatedAt, LocalDateTime deletedAt) {
        return Member.builder()
                .id(id)
                .termsInfo(termsInfo)
                .profileImageUrl(profileImageUrl)
                .profileThumbnailImageUrl(profileThumbnailImageUrl)
                .socialUid(socialUid)
                .loginType(loginType)
                .roleTypes(roleTypes)
                .email(email)
                .nickname(nickname)
                .ageRange(ageRange)
                .birthDay(birthDay)
                .gender(gender)
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
    private Member(Long id, TermsInfo termsInfo, String profileImageUrl, String profileThumbnailImageUrl, String socialUid, LoginType loginType, Set<RoleType> roleTypes, String email, String nickname, LocalDate birthDay, Integer ageRange, Gender gender, LocalDateTime createdAt, LocalDateTime updatedAt, LocalDateTime deletedAt) {
        super(createdAt, updatedAt);
        this.id = id;
        this.termsInfo = termsInfo;
        this.profileImageUrl = profileImageUrl;
        this.profileThumbnailImageUrl = profileThumbnailImageUrl;
        this.socialUid = socialUid;
        this.loginType = loginType;
        this.roleTypes = roleTypes;
        this.email = email;
        this.nickname = nickname;
        this.birthDay = birthDay;
        this.ageRange = ageRange;
        this.gender = gender;
        this.deletedAt = deletedAt;
    }
}
