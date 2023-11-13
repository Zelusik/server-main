package com.zelusik.eatery.domain.member.entity;

import com.zelusik.eatery.domain.favorite_food_category.entity.FavoriteFoodCategory;
import com.zelusik.eatery.domain.member.constant.Gender;
import com.zelusik.eatery.domain.member.constant.LoginType;
import com.zelusik.eatery.domain.member.constant.RoleType;
import com.zelusik.eatery.domain.member.converter.RoleTypesConverter;
import com.zelusik.eatery.global.common.entity.BaseTimeEntity;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import javax.validation.constraints.NotNull;
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

    @Column(nullable = false)
    private String profileImageUrl;

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

    @Column(nullable = false, length = 15)
    private String nickname;

    private LocalDate birthDay;

    private Integer ageRange;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Gender gender;

    private LocalDateTime deletedAt;

    @OneToMany(mappedBy = "member")
    private List<FavoriteFoodCategory> favoriteFoodCategories = new LinkedList<>();

    public Member(@Nullable Long id, @NotNull String profileImageUrl, @NotNull String profileThumbnailImageUrl, @NotNull String socialUid, @NotNull LoginType loginType, @NotNull Set<RoleType> roleTypes, @Nullable String email, @NotNull String nickname, @Nullable LocalDate birthDay, @Nullable Integer ageRange, @Nullable Gender gender, @Nullable LocalDateTime createdAt, @Nullable LocalDateTime updatedAt, @Nullable LocalDateTime deletedAt) {
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

    public static Member create(
            @NotNull String profileImageUrl,
            @NotNull String profileThumbnailImageUrl,
            @NotNull String socialUid,
            @NotNull LoginType loginType,
            @NotNull Set<RoleType> roleTypes,
            @Nullable String email,
            @NotNull String nickname,
            @Nullable Integer ageRange,
            @Nullable Gender gender
    ) {
        return new Member(null, profileImageUrl, profileThumbnailImageUrl, socialUid, loginType, roleTypes, email, nickname, null, ageRange, gender, null, null, null);
    }

    public void update(String profileImageUrl, String profileThumbnailImageUrl, String nickname, LocalDate birthDay, Gender gender) {
        this.profileImageUrl = profileImageUrl;
        this.profileThumbnailImageUrl = profileThumbnailImageUrl;
        this.nickname = nickname;
        this.birthDay = birthDay;
        this.gender = gender;
    }

    public void update(String nickname, LocalDate birthDay, Gender gender) {
        this.nickname = nickname;
        this.birthDay = birthDay;
        this.gender = gender;
    }

    public void rejoin() {
        this.deletedAt = (null);
    }

    public void softDelete() {
        this.deletedAt = LocalDateTime.now();
    }
}
