package com.zelusik.eatery.util;

import com.zelusik.eatery.domain.place.constant.DayOfWeek;
import com.zelusik.eatery.domain.place.constant.KakaoCategoryGroupCode;
import com.zelusik.eatery.domain.review.constant.ReviewKeywordValue;
import com.zelusik.eatery.domain.place.entity.Address;
import com.zelusik.eatery.domain.place.entity.Place;
import com.zelusik.eatery.domain.place.entity.PlaceCategory;
import com.zelusik.eatery.domain.place.entity.Point;
import com.zelusik.eatery.domain.place_menus.entity.PlaceMenus;
import com.zelusik.eatery.domain.opening_hours.dto.OpeningHoursDto;
import com.zelusik.eatery.domain.place.dto.PlaceDto;
import com.zelusik.eatery.domain.place_menus.dto.PlaceMenusDto;
import com.zelusik.eatery.domain.place.dto.request.PlaceCreateRequest;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

public class PlaceTestUtils {

    private PlaceCreateRequest createPlaceRequest() {
        return PlaceCreateRequest.of(
                "308342289",
                "연남토마 본점",
                "http://place.map.kakao.com/308342289",
                KakaoCategoryGroupCode.FD6,
                "음식점 > 퓨전요리 > 퓨전일식",
                "02-332-8064",
                "서울 마포구 연남동 568-26",
                "서울 마포구 월드컵북로6길 61",
                "37.5595073462493",
                "126.921462488105"
        );
    }

    private PlaceDto createPlaceDto() {
        return new PlaceDto(
                1L,
                List.of(ReviewKeywordValue.FRESH),
                "308342289",
                "연남토마 본점",
                "http://place.map.kakao.com/308342289",
                KakaoCategoryGroupCode.FD6,
                PlaceCategory.of("음식점 > 퓨전요리 > 퓨전일식"),
                "02-332-8064",
                Address.of("서울 마포구 연남동 568-26", "서울 마포구 월드컵북로6길 61"),
                "http://place.map.kakao.com/308342289",
                new Point("37.5595073462493", "126.921462488105"),
                null,
                List.of(
                        createOpeningHoursDto(1L, DayOfWeek.MON, LocalTime.of(12, 0), LocalTime.of(18, 0)),
                        createOpeningHoursDto(1L, DayOfWeek.TUE, LocalTime.of(12, 0), LocalTime.of(18, 0)),
                        createOpeningHoursDto(1L, DayOfWeek.WED, LocalTime.of(12, 0), LocalTime.of(18, 0))
                ),
                null,
                false
        );
    }

    private PlaceDto createPlaceDto(Long placeId) {
        return new PlaceDto(
                placeId,
                List.of(ReviewKeywordValue.FRESH),
                "308342289",
                "연남토마 본점",
                "https://place.map.kakao.com/308342289",
                KakaoCategoryGroupCode.FD6,
                PlaceCategory.of("음식점 > 퓨전요리 > 퓨전일식"),
                "02-332-8064",
                Address.of("서울 마포구 연남동 568-26", "서울 마포구 월드컵북로6길 61"),
                "https://place.map.kakao.com/308342289",
                new Point("37.5595073462493", "126.921462488105"),
                null,
                List.of(
                        createOpeningHoursDto(100L, DayOfWeek.MON, LocalTime.of(12, 0), LocalTime.of(18, 0)),
                        createOpeningHoursDto(101L, DayOfWeek.TUE, LocalTime.of(12, 0), LocalTime.of(18, 0)),
                        createOpeningHoursDto(102L, DayOfWeek.WED, LocalTime.of(12, 0), LocalTime.of(18, 0))
                ),
                null,
                false
        );
    }

    private PlaceDto createPlaceDtoWithMarkedStatusAndImages() {
        return createPlaceDtoWithMarkedStatusAndImages(1L);
    }

    private PlaceDto createPlaceDtoWithMarkedStatusAndImages(Long placeId) {
        return createPlaceDtoWithMarkedStatusAndImages(placeId, "308342289");
    }

    private PlaceDto createPlaceDtoWithMarkedStatusAndImages(Long placeId, String kakaoPid) {
        return new PlaceDto(
                placeId,
                List.of(ReviewKeywordValue.FRESH),
                kakaoPid,
                "연남토마 본점",
                "https://place.map.kakao.com/308342289",
                KakaoCategoryGroupCode.FD6,
                PlaceCategory.of("음식점 > 퓨전요리 > 퓨전일식"),
                "02-332-8064",
                Address.of("서울 마포구 연남동 568-26", "서울 마포구 월드컵북로6길 61"),
                "https://place.map.kakao.com/308342289",
                new Point("37.5595073462493", "126.921462488105"),
                null,
                List.of(
                        createOpeningHoursDto(100L, DayOfWeek.MON, LocalTime.of(12, 0), LocalTime.of(18, 0)),
                        createOpeningHoursDto(101L, DayOfWeek.TUE, LocalTime.of(12, 0), LocalTime.of(18, 0)),
                        createOpeningHoursDto(102L, DayOfWeek.WED, LocalTime.of(12, 0), LocalTime.of(18, 0))
                ),
                List.of(),
                false
        );
    }

    private Place createNewPlace(String kakaoPid, String name) {
        return createNewPlace(kakaoPid, name, new PlaceCategory("한식", "냉면", null), Address.of("서울 마포구 연남동 568-26", "서울 마포구 월드컵북로6길 61"), "homepage-url", "12.34", "23.45", null);
    }

    private Place createNewPlace(String kakaoPid, String name, PlaceCategory placeCategory, Address address) {
        return createNewPlace(kakaoPid, name, placeCategory, address, "homepage-url", "12.34", "23.45", null);
    }

    private Place createNewPlace(String kakaoPid, String name, String homepageUrl, String lat, String lng, String closingHours) {
        return createNewPlace(kakaoPid, name, new PlaceCategory("한식", "냉면", null), Address.of("서울 마포구 연남동 568-26", "서울 마포구 월드컵북로6길 61"), homepageUrl, lat, lng, closingHours);
    }

    private Place createNewPlace(String kakaoPid, String name, PlaceCategory placeCategory, Address address, String homepageUrl, String lat, String lng, String closingHours) {
        return createPlace(null, kakaoPid, name, placeCategory, address, homepageUrl, lat, lng, closingHours);
    }

    private Place createPlace(Long id, String kakaoPid) {
        return createPlace(id, kakaoPid, null, "37.5595073462493", "126.921462488105", null);
    }

    private Place createPlace(Long id, String kakaoPid, String homepageUrl, String closingHours) {
        return createPlace(id, kakaoPid, homepageUrl, "37.5595073462493", "126.921462488105", closingHours);
    }

    private Place createPlace(Long id, String kakaoPid, String homepageUrl, String lat, String lng, String closingHours) {
        return createPlace(id, kakaoPid, "name for test", homepageUrl, lat, lng, closingHours);
    }

    private Place createPlace(Long id, String kakaoPid, String name, String homepageUrl, String lat, String lng, String closingHours) {
        return createPlace(id, kakaoPid, name, new PlaceCategory("한식", "냉면", null), homepageUrl, lat, lng, closingHours);
    }

    private Place createPlace(Long id, String kakaoPid, String name, PlaceCategory placeCategory, String homepageUrl, String lat, String lng, String closingHours) {
        return createPlace(id, kakaoPid, name, placeCategory, Address.of("서울 마포구 연남동 568-26", "서울 마포구 월드컵북로6길 61"), homepageUrl, lat, lng, closingHours);
    }

    private Place createPlace(Long id, String kakaoPid, String name, PlaceCategory placeCategory, Address address, String homepageUrl, String lat, String lng, String closingHours) {
        return Place.of(
                id,
                List.of(ReviewKeywordValue.FRESH),
                kakaoPid,
                name,
                "https://place.map.kakao.com/308342289",
                KakaoCategoryGroupCode.FD6,
                placeCategory,
                "02-332-8064",
                address,
                homepageUrl,
                new Point(lat, lng),
                closingHours,
                LocalDateTime.now(),
                LocalDateTime.now()
        );
    }

    private OpeningHoursDto createOpeningHoursDto(
            Long id,
            DayOfWeek dayOfWeek,
            LocalTime openAt,
            LocalTime closeAt
    ) {
        return OpeningHoursDto.of(
                id,
                1L,
                dayOfWeek,
                openAt,
                closeAt
        );
    }

    private PlaceMenusDto createPlaceMenusDto(Long id, Long placeId, List<String> menus) {
        return PlaceMenusDto.of(id, placeId, menus);
    }

    private PlaceMenus createPlaceMenus(Long id, Place place, List<String> menus) {
        return PlaceMenus.of(
                id,
                place,
                menus,
                LocalDateTime.now(),
                LocalDateTime.now(),
                1L,
                1L
        );
    }
}
