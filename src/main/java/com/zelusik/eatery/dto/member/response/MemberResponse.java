package com.zelusik.eatery.dto.member.response;

import com.zelusik.eatery.constant.FoodCategory;
import com.zelusik.eatery.constant.member.Gender;
import com.zelusik.eatery.dto.file.response.ImageResponse;
import com.zelusik.eatery.dto.member.MemberDto;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDate;
import java.util.List;

@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Getter
public class MemberResponse {

    @Schema(description = "회원 id(PK)", example = "1")
    private Long id;

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

    @Schema(description = "선호 음식 카테고리 목록", example = "[\"신선한 재료\", \"최고의 맛\"]")
    private List<String> favoriteFoodCategories;

    public static MemberResponse of(Long id, ImageResponse image, String email, String nickname, Gender gender, LocalDate birthDay, List<FoodCategory> favoriteFoodCategories) {
        return new MemberResponse(
                id,
                image,
                email,
                nickname,
                gender.getDescription(),
                birthDay,
                favoriteFoodCategories.stream()
                        .map(FoodCategory::getName)
                        .toList()
        );
    }

    public static MemberResponse from(MemberDto dto) {
        return of(
                dto.getId(),
                ImageResponse.of(dto.getProfileImageUrl(), dto.getProfileThumbnailImageUrl()),
                dto.getEmail(),
                dto.getNickname(),
                dto.getGender(),
                dto.getBirthDay(),
                dto.getFavoriteFoodCategories()
        );
    }
}
