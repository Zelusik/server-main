package com.zelusik.eatery.dto.place.response;

import com.zelusik.eatery.domain.place.Address;
import com.zelusik.eatery.domain.place.Point;
import com.zelusik.eatery.dto.place.PlaceDto;
import com.zelusik.eatery.dto.review.ReviewImageDto;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PRIVATE)
@Getter
public class FindMarkedPlacesResponse {

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

    @Schema(description = "장소에 대표 이미지(최대 4개)")
    private List<PlaceImageResponse> images;

    public static FindMarkedPlacesResponse from(PlaceDto dto) {
        String category = dto.getCategory().getSecondCategory();
        if (category == null) {
            category = dto.getCategory().getFirstCategory();
        }

        List<PlaceImageResponse> placeImages = List.of();
        if (dto.getImages() != null) {
            placeImages = dto.getImages().stream()
                    .map(PlaceImageResponse::from)
                    .toList();
        }

        return new FindMarkedPlacesResponse(
                dto.getId(),
                dto.getName(),
                category,
                dto.getAddress(),
                dto.getPoint(),
                placeImages
        );
    }

    @AllArgsConstructor
    @NoArgsConstructor(access = AccessLevel.PRIVATE)
    @Getter
    private static class PlaceImageResponse {

        @Schema(description = "이미지 url", example = "https://place-image-url")
        private String url;

        @Schema(description = "썸네일 이미지 url", example = "https://place-thumbnail-image-url")
        private String thumbnailUrl;

        public static PlaceImageResponse from(ReviewImageDto reviewImageDto) {
            return new PlaceImageResponse(reviewImageDto.getUrl(), reviewImageDto.getThumbnailUrl());
        }
    }
}
