package com.zelusik.eatery.dto.place.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Getter
public class PlaceFilteringKeywordListResponse {

    @Schema(description = "Filtering keyword 목록")
    private List<PlaceFilteringKeywordResponse> keywords;

    public static PlaceFilteringKeywordListResponse of(List<PlaceFilteringKeywordResponse> keywords) {
        return new PlaceFilteringKeywordListResponse(keywords);
    }
}
