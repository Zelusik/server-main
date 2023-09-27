package com.zelusik.eatery.domain.place_menus.dto;

import com.zelusik.eatery.domain.place_menus.entity.PlaceMenus;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@AllArgsConstructor(access = AccessLevel.PRIVATE)
@NoArgsConstructor(access = AccessLevel.PRIVATE)
@Getter
public class PlaceMenusDto {

    private Long id;
    private Long placeId;
    private List<String> menus;

    public static PlaceMenusDto of(Long id, Long placeId, List<String> menus) {
        return new PlaceMenusDto(id, placeId, menus);
    }

    public static PlaceMenusDto from(PlaceMenus entity) {
        return of(entity.getId(), entity.getPlace().getId(), entity.getMenus());
    }
}
