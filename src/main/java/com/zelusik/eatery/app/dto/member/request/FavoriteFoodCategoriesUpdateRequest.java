package com.zelusik.eatery.app.dto.member.request;

import com.zelusik.eatery.app.constant.FoodCategory;
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

    @Schema(description = "좋아하는 음식 카테고리 목록")
    @NotNull
    private List<FoodCategory> favoriteFoodCategories;

    public static FavoriteFoodCategoriesUpdateRequest of(List<FoodCategory> favoriteFoodCategories) {
        return new FavoriteFoodCategoriesUpdateRequest(favoriteFoodCategories);
    }
}
