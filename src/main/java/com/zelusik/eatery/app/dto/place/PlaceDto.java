package com.zelusik.eatery.app.dto.place;

import com.zelusik.eatery.app.domain.constant.KakaoCategoryGroupCode;
import com.zelusik.eatery.app.domain.place.*;

import java.time.LocalDateTime;
import java.util.Set;
import java.util.stream.Collectors;

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
        Set<OpeningHoursDto> openingHoursDtos,
        LocalDateTime createdAt,
        LocalDateTime updatedAt,
        LocalDateTime deletedAt
) {
    public static PlaceDto of(String kakaoPid, String name, String pageUrl, KakaoCategoryGroupCode categoryGroupCode, PlaceCategory category, String phone, Address address, String homepageUrl, Point point) {
        return of(null, kakaoPid, name, pageUrl, categoryGroupCode, category, phone, address, homepageUrl, point, null, null, null, null);
    }

    public static PlaceDto of(Long id, String kakaoPid, String name, String pageUrl, KakaoCategoryGroupCode categoryGroupCode, PlaceCategory category, String phone, Address address, String homepageUrl, Point point, Set<OpeningHoursDto> openingHoursDtos, LocalDateTime createdAt, LocalDateTime updatedAt, LocalDateTime deletedAt) {
        return new PlaceDto(id, kakaoPid, name, pageUrl, categoryGroupCode, category, phone, address, homepageUrl, point, openingHoursDtos, createdAt, updatedAt, deletedAt);
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
                place.getOpeningHoursSet().stream()
                        .map(OpeningHoursDto::from)
                        .collect(Collectors.toUnmodifiableSet()),
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
                this.point()
        );
    }
}
