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
        Boolean isMarked,
        LocalDateTime createdAt,
        LocalDateTime updatedAt,
        LocalDateTime deletedAt
) {
    public static PlaceDto of(String kakaoPid, String name, String pageUrl, KakaoCategoryGroupCode categoryGroupCode, PlaceCategory category, String phone, Address address, String homepageUrl, Point point, String closingHours) {
        return of(null, kakaoPid, name, pageUrl, categoryGroupCode, category, phone, address, homepageUrl, point, closingHours, null, null, null, null, null);
    }

    public static PlaceDto of(Long id, String kakaoPid, String name, String pageUrl, KakaoCategoryGroupCode categoryGroupCode, PlaceCategory category, String phone, Address address, String homepageUrl, Point point, String closingHours, List<OpeningHoursDto> openingHoursDtos, Boolean isMarked, LocalDateTime createdAt, LocalDateTime updatedAt, LocalDateTime deletedAt) {
        return new PlaceDto(id, kakaoPid, name, pageUrl, categoryGroupCode, category, phone, address, homepageUrl, point, closingHours, openingHoursDtos, isMarked, createdAt, updatedAt, deletedAt);
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
                null,
                place.getCreatedAt(),
                place.getUpdatedAt(),
                place.getDeletedAt()
        );
    }

    public static PlaceDto from(Place place, List<Long> markedPlaceIdList) {
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
                isMarked(place, markedPlaceIdList),
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

    /**
     * 북마크에 저장한 장소인지 확인하여, 마킹 여부를 반환한다.
     *
     * @param place             확인할 장소
     * @param markedPlaceIdList 북마크에 저장한 장소들의 id 목록
     * @return 마킹 여부
     */
    private static boolean isMarked(Place place, List<Long> markedPlaceIdList) {
        return markedPlaceIdList.contains(place.getId());
    }
}
