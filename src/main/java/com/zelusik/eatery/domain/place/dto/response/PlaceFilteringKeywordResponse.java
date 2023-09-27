package com.zelusik.eatery.domain.place.dto.response;

import com.zelusik.eatery.domain.place.constant.FilteringType;
import com.zelusik.eatery.domain.place.dto.PlaceFilteringKeywordDto;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Getter
public class PlaceFilteringKeywordResponse {

    @Schema(description = "<p>Filtering keyword." +
            "<p> 가능한 항목은 음식 카테고리, 장소의 top 3 keyword, 주소(구, 동 단위)이다.",
            example = "연남동")
    private String keyword;

    private FilteringType type;

    public static PlaceFilteringKeywordResponse of(String keyword, FilteringType type) {
        return new PlaceFilteringKeywordResponse(keyword, type);
    }

    public static PlaceFilteringKeywordResponse from(PlaceFilteringKeywordDto dto) {
        return of(dto.getKeyword(), dto.getType());
    }
}
