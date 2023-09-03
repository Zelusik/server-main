package com.zelusik.eatery.converter;

import com.zelusik.eatery.constant.review.ReviewKeywordValue;

import javax.persistence.AttributeConverter;
import javax.persistence.Converter;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Converter
public class ReviewKeywordValueConverter implements AttributeConverter<List<ReviewKeywordValue>, String> {

    @Override
    public String convertToDatabaseColumn(List<ReviewKeywordValue> attribute) {
        if (attribute == null || attribute.size() == 0) {
            return "";
        }

        return attribute.stream()
                .map(ReviewKeywordValue::toString)
                .collect(Collectors.joining(" "));
    }

    @Override
    public List<ReviewKeywordValue> convertToEntityAttribute(String dbData) {
        if (dbData == null || dbData.isEmpty()) {
            return List.of();
        }

        return Arrays.stream(dbData.split(" "))
                .map(ReviewKeywordValue::valueOf)
                .toList();
    }
}
