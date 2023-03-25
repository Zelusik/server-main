package com.zelusik.eatery.app.dto.place.response;

import com.zelusik.eatery.app.constant.review.ReviewKeywordValue;
import com.zelusik.eatery.app.domain.place.Address;
import com.zelusik.eatery.app.domain.place.Point;
import com.zelusik.eatery.app.dto.place.PlaceDtoWithImages;
import com.zelusik.eatery.app.dto.review.ReviewFileDto;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.LinkedList;
import java.util.List;

@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Getter
public class PlaceResponseWithImages {

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

    @Schema(description = "대표번호", example = "02-332-8064")
    private String phone;

    @Schema(description = "주소")
    private Address address;

    @Schema(description = "인스타그램 url", example = "www.instagram.com/toma_wv/")
    private String snsUrl;

    @Schema(description = "좌표")
    private Point point;

    @Schema(description = "휴무일", example = "금요일")
    private String closingHours;

    @Schema(description = "영업시간 정보")
    private List<OpeningHoursResponse> openingHours;

    @Schema(description = "장소에 대한 이미지")
    private List<String> images;

    @Schema(description = "북마크 여부", example = "false")
    private Boolean isMarked;

    public static PlaceResponseWithImages of(Long id, List<String> top3Keywords, String name, String category, String phone, Address address, String snsUrl, Point point, String closingHours, List<OpeningHoursResponse> openingHours, List<String> images, Boolean isMarked) {
        return new PlaceResponseWithImages(id, top3Keywords, name, category, phone, address, snsUrl, point, closingHours, openingHours, images, isMarked);
    }

    public static PlaceResponseWithImages from(PlaceDtoWithImages dto) {
        String snsUrl = dto.homepageUrl();
        if (snsUrl != null && !snsUrl.contains("instagram")) {
            snsUrl = null;
        }

        String category = dto.category().getSecondCategory();
        if (category == null) {
            category = dto.category().getFirstCategory();
        }

        List<String> images = new LinkedList<>(dto.images().stream()
                .map(ReviewFileDto::url)
                .toList());
        while (images.size() < 3) {
            images.add("");
        }

        return new PlaceResponseWithImages(
                dto.id(),
                dto.top3Keywords().stream()
                        .map(ReviewKeywordValue::getDescription)
                        .toList(),
                dto.name(),
                category,
                dto.phone(),
                dto.address(),
                snsUrl,
                dto.point(),
                dto.closingHours(),
                dto.openingHoursDtos().stream()
                        .map(OpeningHoursResponse::from)
                        .toList(),
                images,
                dto.isMarked()
        );
    }
}
