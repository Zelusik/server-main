package com.zelusik.eatery.app.util.domain;

import com.zelusik.eatery.app.constant.review.ReviewKeyword;

import javax.persistence.AttributeConverter;
import javax.persistence.Converter;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Converter
public class ReviewKeywordsConverter implements AttributeConverter<List<ReviewKeyword>, String> {

    @Override
    public String convertToDatabaseColumn(List<ReviewKeyword> attribute) {
        if (attribute == null || attribute.size() == 0) {
            return null;
        }

        return attribute.stream()
                .map(ReviewKeyword::toString)
                .collect(Collectors.joining(" "));
    }

    @Override
    public List<ReviewKeyword> convertToEntityAttribute(String dbData) {
        if (dbData == null || dbData.isEmpty()) {
            return List.of();
        }

        return Arrays.stream(dbData.split(" "))
                .map(ReviewKeyword::valueOf)
                .toList();
    }
}
