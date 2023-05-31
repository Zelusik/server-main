package com.zelusik.eatery.dto.place;

import com.zelusik.eatery.constant.place.KakaoCategoryGroupCode;
import com.zelusik.eatery.constant.review.ReviewKeywordValue;
import com.zelusik.eatery.domain.place.Address;
import com.zelusik.eatery.domain.place.Place;
import com.zelusik.eatery.domain.place.PlaceCategory;
import com.zelusik.eatery.domain.place.Point;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.List;

@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Getter
public class PlaceDtoWithMarkedStatus {

    private Long id;
    private List<ReviewKeywordValue> top3Keywords;
    private String kakaoPid;
    private String name;
    private String pageUrl;
    private KakaoCategoryGroupCode categoryGroupCode;
    private PlaceCategory category;
    private String phone;
    private Address address;
    private String homepageUrl;
    private Point point;
    private String closingHours;
    private List<OpeningHoursDto> openingHoursDtos;
    private Boolean isMarked;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public static PlaceDtoWithMarkedStatus of(Long id, List<ReviewKeywordValue> top3Keywords, String kakaoPid, String name, String pageUrl, KakaoCategoryGroupCode categoryGroupCode, PlaceCategory category, String phone, Address address, String homepageUrl, Point point, String closingHours, List<OpeningHoursDto> openingHoursDtos, Boolean isMarked, LocalDateTime createdAt, LocalDateTime updatedAt) {
        return new PlaceDtoWithMarkedStatus(id, top3Keywords, kakaoPid, name, pageUrl, categoryGroupCode, category, phone, address, homepageUrl, point, closingHours, openingHoursDtos, isMarked, createdAt, updatedAt);
    }

    public static PlaceDtoWithMarkedStatus from(Place place, boolean isMarked) {
        return of(
                place.getId(),
                place.getTop3Keywords(),
                place.getKakaoPid(),
                place.getName(),
                place.getPageUrl(),
                place.getCategoryGroupCode(),
                place.getCategory(),
                place.getPhone(),
                place.getAddress(),
                place.getHomepageUrl(),
                place.getPoint(),
                place.getClosingHours(),
                place.getOpeningHoursList().stream()
                        .map(OpeningHoursDto::from)
                        .toList(),
                isMarked,
                place.getCreatedAt(),
                place.getUpdatedAt()
        );
    }
}
