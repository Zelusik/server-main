package com.zelusik.eatery.domain.place.dto.response;

import com.zelusik.eatery.domain.place.entity.Address;
import com.zelusik.eatery.domain.place.entity.Point;
import com.zelusik.eatery.domain.place.dto.PlaceDto;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PRIVATE)
@Getter
public class SearchPlacesByKeywordResponse {

    @Schema(description = "PK of Place", example = "3")
    private Long id;

    @Schema(description = "장소 이름", example = "강남돈까스")
    private String name;

    @Schema(description = "주소")
    private Address address;

    @Schema(description = "좌표 (위도, 경도)")
    private Point point;

    public static SearchPlacesByKeywordResponse from(PlaceDto placeDto) {
        return new SearchPlacesByKeywordResponse(
                placeDto.getId(),
                placeDto.getName(),
                placeDto.getAddress(),
                placeDto.getPoint()
        );
    }
}
