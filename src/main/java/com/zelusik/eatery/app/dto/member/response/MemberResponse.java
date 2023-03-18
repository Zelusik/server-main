package com.zelusik.eatery.app.dto.member.response;

import com.zelusik.eatery.app.constant.FoodCategory;
import com.zelusik.eatery.app.dto.member.MemberDto;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Getter
public class MemberResponse {

    @Schema(description = "회원 id(PK)", example = "1")
    private Long id;

    @Schema(description = "닉네임", example = "우기")
    private String nickname;

    @Schema(description = "선호 음식 카테고리 목록")
    private List<FoodCategory> favoriteFoodCategories;

    public static MemberResponse of(Long id, String nickname, List<FoodCategory> favoriteFoodCategories) {
        return new MemberResponse(id, nickname, favoriteFoodCategories);
    }

    public static MemberResponse from(MemberDto dto) {
        return of(dto.id(), dto.nickname(), dto.favoriteFoodCategories());
    }
}
