package com.zelusik.eatery.app.dto.place;

import com.zelusik.eatery.app.constant.place.KakaoCategoryGroupCode;
import com.zelusik.eatery.app.domain.place.Address;
import com.zelusik.eatery.app.domain.place.Place;
import com.zelusik.eatery.app.domain.place.PlaceCategory;
import com.zelusik.eatery.app.domain.place.Point;

import java.time.LocalDateTime;
import java.util.List;

public record PlaceDto(
        Long id,
        String kakaoPid,
        String name,
        String pageUrl,
        KakaoCategoryGroupCode categoryGroupCode,
        PlaceCategory category,
        String phone,
        Address address,
        String homepageUrl,
        Point point,
        String closingHours,
        List<OpeningHoursDto> openingHoursDtos,
        LocalDateTime createdAt,
        LocalDateTime updatedAt,
        LocalDateTime deletedAt
) {
    public static PlaceDto of(String kakaoPid, String name, String pageUrl, KakaoCategoryGroupCode categoryGroupCode, PlaceCategory category, String phone, Address address, String homepageUrl, Point point, String closingHours) {
        return of(null, kakaoPid, name, pageUrl, categoryGroupCode, category, phone, address, homepageUrl, point, closingHours, null, null, null, null);
    }

    public static PlaceDto of(Long id, String kakaoPid, String name, String pageUrl, KakaoCategoryGroupCode categoryGroupCode, PlaceCategory category, String phone, Address address, String homepageUrl, Point point, String closingHours, List<OpeningHoursDto> openingHoursDtos, LocalDateTime createdAt, LocalDateTime updatedAt, LocalDateTime deletedAt) {
        return new PlaceDto(id, kakaoPid, name, pageUrl, categoryGroupCode, category, phone, address, homepageUrl, point, closingHours, openingHoursDtos, createdAt, updatedAt, deletedAt);
    }

    public static PlaceDto from(Place place) {
        return of(
                place.getId(),
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
                place.getCreatedAt(),
                place.getUpdatedAt(),
                place.getDeletedAt()
        );
    }

    public Place toEntity() {
        return Place.of(
                this.kakaoPid(),
                this.name(),
                this.pageUrl(),
                this.categoryGroupCode(),
                this.category(),
                this.phone(),
                this.address(),
                this.homepageUrl(),
                this.point(),
                this.closingHours()
        );
    }
}
