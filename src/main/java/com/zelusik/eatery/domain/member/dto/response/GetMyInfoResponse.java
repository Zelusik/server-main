package com.zelusik.eatery.domain.member.dto.response;

import com.zelusik.eatery.global.common.constant.FoodCategoryValue;
import com.zelusik.eatery.domain.member.dto.MemberDto;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.List;

@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PRIVATE)
@Getter
public class GetMyInfoResponse {

    @Schema(description = "회원 id(PK)", example = "1")
    private Long id;

    @Schema(description = "프로필 이미지")
    private MemberProfileImageResponse profileImage;

    @Schema(description = "닉네임", example = "우기")
    private String nickname;

    @Schema(description = "성별", example = "남성")
    private String gender;

    @Schema(description = "생년월일", example = "1998-01-05")
    private LocalDate birthDay;

    @Schema(description = "선호 음식 카테고리 목록")
    private List<FoodCategoryResponse> favoriteFoodCategories;

    public static GetMyInfoResponse from(MemberDto memberDto) {
        return new GetMyInfoResponse(
                memberDto.getId(),
                new MemberProfileImageResponse(memberDto.getProfileImageUrl(), memberDto.getProfileThumbnailImageUrl()),
                memberDto.getNickname(),
                memberDto.getGender().getDescription(),
                memberDto.getBirthDay(),
                memberDto.getFavoriteFoodCategories().stream()
                        .map(FoodCategoryResponse::from)
                        .toList()
        );
    }

    @AllArgsConstructor(access = AccessLevel.PRIVATE)
    @NoArgsConstructor(access = AccessLevel.PRIVATE)
    @Getter
    public static class MemberProfileImageResponse {

        @Schema(description = "이미지 url", example = "https://member-profile-image-url")
        private String imageUrl;

        @Schema(description = "썸네일 이미지 url", example = "https://member-profile-thumbnail-image-url")
        private String thumbnailImageUrl;
    }

    @AllArgsConstructor(access = AccessLevel.PRIVATE)
    @NoArgsConstructor(access = AccessLevel.PRIVATE)
    @Getter
    private static class FoodCategoryResponse {

        @Schema(description = "value of food category", example = "KOREAN")
        private String value;

        @Schema(description = "카테고리 이름", example = "한식")
        private String categoryName;

        private static FoodCategoryResponse from(FoodCategoryValue foodCategoryValue) {
            return new FoodCategoryResponse(foodCategoryValue.name(), foodCategoryValue.getCategoryName());
        }
    }
}
