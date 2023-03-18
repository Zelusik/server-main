package com.zelusik.eatery.app.util.domain;

import com.zelusik.eatery.app.constant.FoodCategory;

import javax.persistence.AttributeConverter;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class FoodCategoryConverter implements AttributeConverter<List<FoodCategory>, String> {

    @Override
    public String convertToDatabaseColumn(List<FoodCategory> attribute) {
        if (attribute == null || attribute.isEmpty()) {
            return null;
        }

        return attribute.stream()
                .map(FoodCategory::toString)
                .collect(Collectors.joining(" "));
    }

    @Override
    public List<FoodCategory> convertToEntityAttribute(String dbData) {
        if (dbData == null || dbData.isEmpty()) {
            return List.of();
        }

        return Arrays.stream(dbData.split(" "))
                .map(FoodCategory::valueOf)
                .toList();
    }
}
