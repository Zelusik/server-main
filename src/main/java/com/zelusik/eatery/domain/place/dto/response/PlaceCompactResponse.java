package com.zelusik.eatery.domain.place.dto.response;

import com.zelusik.eatery.domain.place.dto.PlaceWithMarkedStatusDto;
import com.zelusik.eatery.domain.place.entity.Address;
import com.zelusik.eatery.domain.review.constant.ReviewKeywordValue;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Getter
public class PlaceCompactResponse {

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

    @Schema(description = "북마크 여부", example = "false")
    private Boolean isMarked;

    public static PlaceCompactResponse of(Long id, List<String> top3Keywords, String name, String category, Address address, Boolean isMarked) {
        return new PlaceCompactResponse(id, top3Keywords, name, category, address, isMarked);
    }

    public static PlaceCompactResponse from(PlaceWithMarkedStatusDto dto) {
        String category = dto.getCategory().getSecondCategory();
        if (category == null) {
            category = dto.getCategory().getFirstCategory();
        }

        return new PlaceCompactResponse(
                dto.getId(),
                dto.getTop3Keywords().stream()
                        .map(ReviewKeywordValue::getContent)
                        .toList(),
                dto.getName(),
                category,
                dto.getAddress(),
                dto.getIsMarked()
        );
    }
}
