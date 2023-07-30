package com.zelusik.eatery.converter;

import org.springframework.lang.Nullable;

import javax.persistence.AttributeConverter;
import java.util.Arrays;
import java.util.List;

public class PlaceMenusConverter implements AttributeConverter<List<String>, String> {

    private static final String DELIMITER = ",";

    @Override
    public String convertToDatabaseColumn(@Nullable List<String> attribute) {
        if (attribute == null || attribute.size() == 0) {
            return null;
        }
        return String.join(DELIMITER, attribute);
    }

    @Override
    public List<String> convertToEntityAttribute(@Nullable String dbData) {
        if (dbData == null || dbData.isBlank()) {
            return List.of();
        }
        return Arrays.stream(dbData.split(DELIMITER)).toList();
    }
}
