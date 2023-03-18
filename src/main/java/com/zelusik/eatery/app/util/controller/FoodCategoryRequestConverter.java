package com.zelusik.eatery.app.util.controller;

import com.zelusik.eatery.app.constant.FoodCategory;
import org.springframework.core.convert.converter.Converter;

public class FoodCategoryRequestConverter implements Converter<String, FoodCategory> {

    @Override
    public FoodCategory convert(String source) {
        return FoodCategory.valueOfDescription(source);
    }
}
