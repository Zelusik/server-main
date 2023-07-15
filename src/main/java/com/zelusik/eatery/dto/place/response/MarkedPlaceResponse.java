package com.zelusik.eatery.dto.place.response;

import com.zelusik.eatery.domain.place.Address;
import com.zelusik.eatery.domain.place.Point;
import com.zelusik.eatery.dto.file.response.ImageResponse;
import com.zelusik.eatery.dto.place.PlaceDto;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Getter
public class MarkedPlaceResponse {

    @Schema(description = "장소의 id(PK)", example = "1")
    private Long id;

    @Schema(description = "이름", example = "연남토마 본점")
    private String name;

    @Schema(description = "카테고리", example = "퓨전일식")
    private String category;

    @Schema(description = "주소")
    private Address address;

    @Schema(description = "위치 정보")
    private Point point;

    @Schema(description = "장소에 대한 이미지")
    private List<ImageResponse> images;

    public static MarkedPlaceResponse of(Long id, String name, String category, Address address, Point point, List<ImageResponse> images) {
        return new MarkedPlaceResponse(id, name, category, address, point, images);
    }

    public static MarkedPlaceResponse from(PlaceDto dto) {

        String category = dto.getCategory().getSecondCategory();
        if (category == null) {
            category = dto.getCategory().getFirstCategory();
        }

        List<ImageResponse> images = dto.getImages().stream()
                .map(reviewImageDto -> ImageResponse.of(reviewImageDto.getUrl(), reviewImageDto.getThumbnailUrl()))
                .toList();

        return MarkedPlaceResponse.of(
                dto.getId(),
                dto.getName(),
                category,
                dto.getAddress(),
                dto.getPoint(),
                images
        );
    }
}
