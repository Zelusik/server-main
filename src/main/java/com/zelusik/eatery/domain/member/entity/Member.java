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
import org.springframework.lang.Nullable;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
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

    @NotBlank
    private String profileImageUrl;

    @NotBlank
    private String profileThumbnailImageUrl;

    @NotBlank
    @Column(unique = true)
    private String socialUid;

    @NotNull
    @Enumerated(EnumType.STRING)
    private LoginType loginType;

    @NotEmpty
    @Convert(converter = RoleTypesConverter.class)
    private Set<RoleType> roleTypes;

    private String email;

    @NotNull
    @Embedded
    private MemberNickname nickname;

    private LocalDate birthDay;

    private Integer ageRange;

    @NotNull
    @Enumerated(EnumType.STRING)
    private Gender gender;

    private LocalDateTime deletedAt;

    @OneToMany(mappedBy = "member")
    private List<FavoriteFoodCategory> favoriteFoodCategories = new LinkedList<>();

    public Member(
            @Nullable Long id,
            @NotNull String profileImageUrl,
            @NotNull String profileThumbnailImageUrl,
            @NotNull String socialUid,
            @NotNull LoginType loginType,
            @NotNull Set<RoleType> roleTypes,
            @Nullable String email,
            @NotNull String nickname,
            @Nullable LocalDate birthDay,
            @Nullable Integer ageRange,
            @Nullable Gender gender,
            @Nullable LocalDateTime createdAt,
            @Nullable LocalDateTime updatedAt,
            @Nullable LocalDateTime deletedAt
    ) {
        super(createdAt, updatedAt);
        this.id = id;
        this.profileImageUrl = profileImageUrl;
        this.profileThumbnailImageUrl = profileThumbnailImageUrl;
        this.socialUid = socialUid;
        this.loginType = loginType;
        this.roleTypes = roleTypes;
        this.email = email;
        this.nickname = new MemberNickname(nickname);
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

    public String getNickname() {
        return nickname.getNickname();
    }

    public void update(String profileImageUrl, String profileThumbnailImageUrl, String nickname, LocalDate birthDay, Gender gender) {
        this.profileImageUrl = profileImageUrl;
        this.profileThumbnailImageUrl = profileThumbnailImageUrl;
        this.nickname = new MemberNickname(nickname);
        this.birthDay = birthDay;
        this.gender = gender;
    }

    public void update(String nickname, LocalDate birthDay, Gender gender) {
        this.nickname = new MemberNickname(nickname);
        this.birthDay = birthDay;
        this.gender = gender;
    }

    public void rejoin() {
        this.deletedAt = null;
    }

    public void softDelete() {
        this.deletedAt = LocalDateTime.now();
    }
}
