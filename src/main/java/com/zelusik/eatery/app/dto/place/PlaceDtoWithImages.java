package com.zelusik.eatery.app.dto.place;

import com.zelusik.eatery.app.constant.place.KakaoCategoryGroupCode;
import com.zelusik.eatery.app.constant.review.ReviewKeywordValue;
import com.zelusik.eatery.app.domain.place.Address;
import com.zelusik.eatery.app.domain.place.Place;
import com.zelusik.eatery.app.domain.place.PlaceCategory;
import com.zelusik.eatery.app.domain.place.Point;
import com.zelusik.eatery.app.dto.review.ReviewFileDto;

import java.time.LocalDateTime;
import java.util.List;

public record PlaceDtoWithImages(
        Long id,
        List<ReviewKeywordValue> top3Keywords,
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
        List<ReviewFileDto> images,
        Boolean isMarked,
        LocalDateTime createdAt,
        LocalDateTime updatedAt,
        LocalDateTime deletedAt
) {
    public static PlaceDtoWithImages of(String kakaoPid, String name, String pageUrl, KakaoCategoryGroupCode categoryGroupCode, PlaceCategory category, String phone, Address address, String homepageUrl, Point point, String closingHours) {
        return of(null, null, kakaoPid, name, pageUrl, categoryGroupCode, category, phone, address, homepageUrl, point, closingHours, null, null, null, null, null, null);
    }

    public static PlaceDtoWithImages of(Long id, List<ReviewKeywordValue> top3Keywords, String kakaoPid, String name, String pageUrl, KakaoCategoryGroupCode categoryGroupCode, PlaceCategory category, String phone, Address address, String homepageUrl, Point point, String closingHours, List<OpeningHoursDto> openingHoursDtos, List<ReviewFileDto> images, Boolean isMarked, LocalDateTime createdAt, LocalDateTime updatedAt, LocalDateTime deletedAt) {
        return new PlaceDtoWithImages(id, top3Keywords, kakaoPid, name, pageUrl, categoryGroupCode, category, phone, address, homepageUrl, point, closingHours, openingHoursDtos, images, isMarked, createdAt, updatedAt, deletedAt);
    }

    public static PlaceDtoWithImages from(Place place, List<ReviewFileDto> images, List<Long> markedPlaceIdList) {
        Boolean isMarked = markedPlaceIdList != null ? isMarked(place, markedPlaceIdList) : null;

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
                images,
                isMarked,
                place.getCreatedAt(),
                place.getUpdatedAt(),
                place.getDeletedAt()
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
