package com.zelusik.eatery.domain.place.dto;

import com.zelusik.eatery.domain.place.constant.FilteringType;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PRIVATE)
@Getter
public class PlaceFilteringKeywordDto {

    private String keyword;
    private int count;
    private FilteringType type;
}
