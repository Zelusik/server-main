package com.zelusik.eatery.dto.place.response;

import com.zelusik.eatery.constant.review.ReviewKeywordValue;
import com.zelusik.eatery.domain.place.Address;
import com.zelusik.eatery.domain.place.Point;
import com.zelusik.eatery.dto.file.response.ImageResponse;
import com.zelusik.eatery.dto.place.PlaceDtoWithMarkedStatusAndImages;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Getter
public class PlaceCompactResponseWithImages {

    @Schema(description = "장소의 id(PK)", example = "1")
    private Long id;

    @Schema(
            description = "<p>가장 많이 태그된 top 3 keywords." +
                    "<p>이 장소에 대한 리뷰가 없다면, empty array로 응답합니다.",
            example = "[\"신선한 재료\", \"최고의 맛\"]"
    )
    List<String> top3Keywords;

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

    @Schema(description = "북마크 여부", example = "false")
    private Boolean isMarked;

    public static PlaceCompactResponseWithImages of(Long id, List<String> top3Keywords, String name, String category, Address address, Point point, List<ImageResponse> images, Boolean isMarked) {
        return new PlaceCompactResponseWithImages(id, top3Keywords, name, category, address, point, images, isMarked);
    }

    public static PlaceCompactResponseWithImages from(PlaceDtoWithMarkedStatusAndImages dto) {
        String category = dto.getCategory().getSecondCategory();
        if (category == null) {
            category = dto.getCategory().getFirstCategory();
        }

        return PlaceCompactResponseWithImages.of(
                dto.getId(),
                dto.getTop3Keywords().stream()
                        .map(ReviewKeywordValue::getDescription)
                        .toList(),
                dto.getName(),
                category,
                dto.getAddress(),
                dto.getPoint(),
                dto.getImages().stream()
                        .map(reviewImageDto -> ImageResponse.of(reviewImageDto.getUrl(), reviewImageDto.getThumbnailUrl()))
                        .toList(),
                dto.getIsMarked()
        );
    }
}
