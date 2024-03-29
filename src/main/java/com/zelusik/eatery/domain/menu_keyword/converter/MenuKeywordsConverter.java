package com.zelusik.eatery.domain.menu_keyword.converter;

import org.springframework.lang.Nullable;

import javax.persistence.AttributeConverter;
import java.util.Arrays;
import java.util.List;

public class MenuKeywordsConverter implements AttributeConverter<List<String>, String> {

    private static final String DELIMITER = ",";

    @Override
    public String convertToDatabaseColumn(@Nullable List<String> attribute) {
        if (attribute == null || attribute.isEmpty()) {
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
