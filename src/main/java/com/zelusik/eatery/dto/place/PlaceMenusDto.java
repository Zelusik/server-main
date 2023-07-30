package com.zelusik.eatery.dto.place;

import com.zelusik.eatery.domain.place.PlaceMenus;
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

    // 장소 entity를 DB에서 조회하지 않고 PlaceMenuDto를 생성하기 위해 placeId를 따로 전달받는다.
    public static PlaceMenusDto from(PlaceMenus entity, Long placeId) {
        return of(entity.getId(), placeId, entity.getMenus());
    }
}
