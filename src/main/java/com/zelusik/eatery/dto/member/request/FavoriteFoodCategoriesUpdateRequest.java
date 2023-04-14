package com.zelusik.eatery.dto.member.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;
import java.util.List;

@AllArgsConstructor(access = AccessLevel.PRIVATE)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public class FavoriteFoodCategoriesUpdateRequest {

    @Schema(description = "좋아하는 음식 카테고리 목록", example = "[\"한식\", \"일식\", \"디저트\"]")
    @NotNull
    private List<String> favoriteFoodCategories;

    public static FavoriteFoodCategoriesUpdateRequest of(List<String> favoriteFoodCategories) {
        return new FavoriteFoodCategoriesUpdateRequest(favoriteFoodCategories);
    }
}
