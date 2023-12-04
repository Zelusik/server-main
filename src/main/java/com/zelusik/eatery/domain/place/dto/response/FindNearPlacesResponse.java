package com.zelusik.eatery.domain.place.dto.response;

import com.zelusik.eatery.domain.place.dto.PlaceWithMarkedStatusAndImagesDto;
import com.zelusik.eatery.domain.place.entity.Address;
import com.zelusik.eatery.domain.place.entity.Point;
import com.zelusik.eatery.domain.review.constant.ReviewKeywordValue;
import com.zelusik.eatery.domain.review_image.dto.ReviewImageDto;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PRIVATE)
@Getter
public class FindNearPlacesResponse {

    @Schema(description = "장소의 id(PK)", example = "1")
    private Long id;

    @Schema(description = "<p>가장 많이 태그된 top 3 keywords." +
                          "<p>이 장소에 대한 리뷰가 없다면, empty array로 응답합니다.",
            example = "[\"신선한 재료\", \"최고의 맛\"]")
    List<String> top3Keywords;

    @Schema(description = "이름", example = "연남토마 본점")
    private String name;

    @Schema(description = "카테고리", example = "퓨전일식")
    private String category;

    @Schema(description = "주소")
    private Address address;

    @Schema(description = "위치 정보")
    private Point point;

    @Schema(description = "장소 대표 이미지(최대 4개)")
    private List<PlaceImageResponse> images;

    @Schema(description = "북마크 여부", example = "false")
    private Boolean isMarked;

    public static FindNearPlacesResponse from(PlaceWithMarkedStatusAndImagesDto dto) {
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

        return new FindNearPlacesResponse(
                dto.getId(),
                dto.getTop3Keywords().stream()
                        .map(ReviewKeywordValue::getContent)
                        .toList(),
                dto.getName(),
                category,
                dto.getAddress(),
                dto.getPoint(),
                placeImages,
                dto.getIsMarked()
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
