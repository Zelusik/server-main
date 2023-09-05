package com.zelusik.eatery.domain.member;

import com.zelusik.eatery.constant.member.Gender;
import com.zelusik.eatery.constant.member.LoginType;
import com.zelusik.eatery.constant.member.RoleType;
import com.zelusik.eatery.converter.RoleTypesConverter;
import com.zelusik.eatery.domain.BaseTimeEntity;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;

import javax.persistence.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@Table(indexes = {
        @Index(name = "idx__member__social_uid", columnList = "socialUid"),
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

    public static Member of(
            @NonNull String profileImageUrl,
            @NonNull String profileThumbnailImageUrl,
            @NonNull String socialUid,
            @NonNull LoginType loginType,
            @NonNull Set<RoleType> roleTypes,
            @Nullable String email,
            @NonNull String nickname,
            @Nullable Integer ageRange,
            @Nullable Gender gender
    ) {
        return of(null, profileImageUrl, profileThumbnailImageUrl, socialUid, loginType, roleTypes, email, nickname, null, ageRange, gender, null, null, null);
    }

    public static Member of(
            @Nullable Long id,
            @NonNull String profileImageUrl,
            @NonNull String profileThumbnailImageUrl,
            @NonNull String socialUid,
            @NonNull LoginType loginType,
            @NonNull Set<RoleType> roleTypes,
            @Nullable String email,
            @NonNull String nickname,
            @Nullable LocalDate birthDay,
            @Nullable Integer ageRange,
            @Nullable Gender gender,
            @Nullable LocalDateTime createdAt,
            @Nullable LocalDateTime updatedAt,
            @Nullable LocalDateTime deletedAt
    ) {
        return new Member(id, profileImageUrl, profileThumbnailImageUrl, socialUid, loginType, roleTypes, email, nickname, birthDay, ageRange, gender, createdAt, updatedAt, deletedAt);
    }

    private Member(@Nullable Long id, @NonNull String profileImageUrl, @NonNull String profileThumbnailImageUrl, @NonNull String socialUid, @NonNull LoginType loginType, @NonNull Set<RoleType> roleTypes, @Nullable String email, @NonNull String nickname, @Nullable LocalDate birthDay, @Nullable Integer ageRange, @Nullable Gender gender, @Nullable LocalDateTime createdAt, @Nullable LocalDateTime updatedAt, @Nullable LocalDateTime deletedAt) {
        super(createdAt, updatedAt);
        this.id = id;
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

    public void softDelete() {
        this.setDeletedAt(LocalDateTime.now());
    }
}
