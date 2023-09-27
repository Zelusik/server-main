package com.zelusik.eatery.domain.favorite_food_category.dto.request;

import com.zelusik.eatery.global.common.constant.FoodCategoryValue;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;
import java.util.List;

@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public class FavoriteFoodCategoriesUpdateRequest {

    @NotNull
    private List<FoodCategoryValue> favoriteFoodCategories;
}
