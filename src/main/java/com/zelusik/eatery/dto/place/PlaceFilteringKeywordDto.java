package com.zelusik.eatery.dto.place;

import com.zelusik.eatery.constant.place.FilteringType;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;

@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PRIVATE)
@Getter
public class PlaceFilteringKeywordDto {

    private String keyword;
    private int count;
    private FilteringType type;
}
