package com.zelusik.eatery.domain.member.dto.response;

import com.zelusik.eatery.global.common.constant.FoodCategoryValue;
import com.zelusik.eatery.domain.member.constant.Gender;
import com.zelusik.eatery.domain.member.constant.RoleType;
import com.zelusik.eatery.global.file.dto.response.ImageResponse;
import com.zelusik.eatery.domain.member.dto.MemberDto;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Getter
public class MemberResponse {

    @Schema(description = "회원 id(PK)", example = "1")
    private Long id;

    @Schema(description = "회원에게 부여된 역할들", example = "사용자, 운영자")
    private String roleTypes;

    @Schema(description = "프로필 이미지")
    private ImageResponse image;

    @Schema(description = "이메일", example = "eatery@gmail.com")
    private String email;

    @Schema(description = "닉네임", example = "우기")
    private String nickname;

    @Schema(description = "성별", example = "남성")
    private String gender;

    @Schema(description = "생년월일", example = "1998-01-05")
    private LocalDate birthDay;

    @Schema(description = "선호 음식 카테고리 목록", example = "[\"한식\", \"중식\"]")
    private List<String> favoriteFoodCategories;

    public static MemberResponse of(Long id, String roleTypes, ImageResponse image, String email, String nickname, Gender gender, LocalDate birthDay, List<FoodCategoryValue> favoriteFoodCategories) {
        return new MemberResponse(
                id,
                roleTypes,
                image,
                email,
                nickname,
                gender.getDescription(),
                birthDay,
                favoriteFoodCategories.stream()
                        .map(FoodCategoryValue::getCategoryName)
                        .toList()
        );
    }

    public static MemberResponse from(MemberDto dto) {
        return of(
                dto.getId(),
                dto.getRoleTypes().stream()
                        .map(RoleType::getDescription)
                        .collect(Collectors.joining(", ")),
                ImageResponse.of(dto.getProfileImageUrl(), dto.getProfileThumbnailImageUrl()),
                dto.getEmail(),
                dto.getNickname(),
                dto.getGender(),
                dto.getBirthDay(),
                dto.getFavoriteFoodCategories()
        );
    }
}
