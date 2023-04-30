package com.zelusik.eatery.dto.place;

import com.zelusik.eatery.constant.place.FilteringType;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Getter
public class PlaceFilteringKeywordDto {

    private String keyword;
    private Integer count;
    private FilteringType type;

    public static PlaceFilteringKeywordDto of(String keyword, Integer count, FilteringType type) {
        return new PlaceFilteringKeywordDto(keyword, count, type);
    }
}
