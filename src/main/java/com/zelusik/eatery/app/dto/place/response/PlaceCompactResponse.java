package com.zelusik.eatery.app.dto.place.response;

import com.zelusik.eatery.app.domain.place.Address;
import com.zelusik.eatery.app.dto.place.PlaceDto;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Getter
public class PlaceCompactResponse {

    @Schema(description = "장소의 id(PK)", example = "1")
    private Long id;

    @Schema(description = "이름", example = "연남토마 본점")
    private String name;

    @Schema(description = "카테고리", example = "퓨전일식")
    private String category;

    @Schema(description = "주소")
    private Address address;

    @Schema(description = "북마크 여부", example = "false")
    private Boolean isMarked;

    public static PlaceCompactResponse of(Long id, String name, String category, Address address, Boolean isMarked) {
        return new PlaceCompactResponse(id, name, category, address, isMarked);
    }

    public static PlaceCompactResponse from(PlaceDto placeDto) {
        String category = placeDto.category().getSecondCategory();
        if (category == null) {
            category = placeDto.category().getFirstCategory();
        }

        return new PlaceCompactResponse(
                placeDto.id(),
                placeDto.name(),
                category,
                placeDto.address(),
                false
        );
    }
}
