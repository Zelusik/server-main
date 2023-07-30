package com.zelusik.eatery.dto.place.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.zelusik.eatery.dto.place.PlaceMenusDto;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PRIVATE)
@Getter
@JsonInclude(JsonInclude.Include.NON_NULL)
public class PlaceMenusResponse {

    @Schema(description = "PK of place menus", example = "4")
    private Long id;

    @Schema(description = "PK of place", example = "3")
    private Long placeId;

    @Schema(description = "메뉴 목록", example = "[\"삼겹살\", \"김치전\", \"비빔 냉면\"]")
    private List<String> menus;

    public static PlaceMenusResponse from(PlaceMenusDto dto) {
        return new PlaceMenusResponse(dto.getId(), dto.getPlaceId(), dto.getMenus());
    }

    public static PlaceMenusResponse fromWithoutIds(PlaceMenusDto dto) {
        return new PlaceMenusResponse(null, null, dto.getMenus());
    }
}
