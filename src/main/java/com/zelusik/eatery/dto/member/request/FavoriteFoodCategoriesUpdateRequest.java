package com.zelusik.eatery.dto.member.request;

import com.zelusik.eatery.constant.FoodCategoryValue;
import io.swagger.v3.oas.annotations.media.Schema;
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
