package com.zelusik.eatery.app.dto.place.response;

import com.zelusik.eatery.app.domain.place.Address;
import com.zelusik.eatery.app.dto.place.PlaceDto;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Getter
public class PlaceCompactResponseWithoutIsMarked {

    @Schema(description = "장소의 id(PK)", example = "1")
    private Long id;

    @Schema(description = "이름", example = "연남토마 본점")
    private String name;

    @Schema(description = "카테고리", example = "퓨전일식")
    private String category;

    @Schema(description = "주소")
    private Address address;

    public static PlaceCompactResponseWithoutIsMarked of(Long id, String name, String category, Address address) {
        return new PlaceCompactResponseWithoutIsMarked(id, name, category, address);
    }

    public static PlaceCompactResponseWithoutIsMarked from(PlaceDto dto) {
        String category = dto.category().getSecondCategory();
        if (category == null) {
            category = dto.category().getFirstCategory();
        }

        return of(dto.id(), dto.name(), category, dto.address());
    }
}
