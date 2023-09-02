package com.zelusik.eatery.util;

import com.zelusik.eatery.constant.place.DayOfWeek;
import com.zelusik.eatery.constant.place.KakaoCategoryGroupCode;
import com.zelusik.eatery.constant.review.ReviewKeywordValue;
import com.zelusik.eatery.domain.place.*;
import com.zelusik.eatery.dto.place.OpeningHoursDto;
import com.zelusik.eatery.dto.place.PlaceDto;
import com.zelusik.eatery.dto.place.PlaceMenusDto;
import com.zelusik.eatery.dto.place.request.PlaceCreateRequest;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

public class PlaceTestUtils {

    public static PlaceCreateRequest createPlaceRequest() {
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

    public static PlaceDto createPlaceDto() {
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

    public static PlaceDto createPlaceDto(Long placeId) {
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

    public static PlaceDto createPlaceDtoWithMarkedStatusAndImages() {
        return createPlaceDtoWithMarkedStatusAndImages(1L);
    }

    public static PlaceDto createPlaceDtoWithMarkedStatusAndImages(Long placeId) {
        return createPlaceDtoWithMarkedStatusAndImages(placeId, "308342289");
    }

    public static PlaceDto createPlaceDtoWithMarkedStatusAndImages(Long placeId, String kakaoPid) {
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

    public static Place createNewPlace(String kakaoPid, String name) {
        return createNewPlace(kakaoPid, name, new PlaceCategory("한식", "냉면", null), Address.of("서울 마포구 연남동 568-26", "서울 마포구 월드컵북로6길 61"), "homepage-url", "12.34", "23.45", null);
    }

    public static Place createNewPlace(String kakaoPid, String name, PlaceCategory placeCategory, Address address) {
        return createNewPlace(kakaoPid, name, placeCategory, address, "homepage-url", "12.34", "23.45", null);
    }

    public static Place createNewPlace(String kakaoPid, String name, String homepageUrl, String lat, String lng, String closingHours) {
        return createNewPlace(kakaoPid, name, new PlaceCategory("한식", "냉면", null), Address.of("서울 마포구 연남동 568-26", "서울 마포구 월드컵북로6길 61"), homepageUrl, lat, lng, closingHours);
    }

    public static Place createNewPlace(String kakaoPid, String name, PlaceCategory placeCategory, Address address, String homepageUrl, String lat, String lng, String closingHours) {
        return createPlace(null, kakaoPid, name, placeCategory, address, homepageUrl, lat, lng, closingHours);
    }

    public static Place createPlace(Long id, String kakaoPid) {
        return createPlace(id, kakaoPid, null, "37.5595073462493", "126.921462488105", null);
    }

    public static Place createPlace(Long id, String kakaoPid, String homepageUrl, String closingHours) {
        return createPlace(id, kakaoPid, homepageUrl, "37.5595073462493", "126.921462488105", closingHours);
    }

    public static Place createPlace(Long id, String kakaoPid, String homepageUrl, String lat, String lng, String closingHours) {
        return createPlace(id, kakaoPid, "name for test", homepageUrl, lat, lng, closingHours);
    }

    public static Place createPlace(Long id, String kakaoPid, String name, String homepageUrl, String lat, String lng, String closingHours) {
        return createPlace(id, kakaoPid, name, new PlaceCategory("한식", "냉면", null), homepageUrl, lat, lng, closingHours);
    }

    public static Place createPlace(Long id, String kakaoPid, String name, PlaceCategory placeCategory, String homepageUrl, String lat, String lng, String closingHours) {
        return createPlace(id, kakaoPid, name, placeCategory, Address.of("서울 마포구 연남동 568-26", "서울 마포구 월드컵북로6길 61"), homepageUrl, lat, lng, closingHours);
    }

    public static Place createPlace(Long id, String kakaoPid, String name, PlaceCategory placeCategory, Address address, String homepageUrl, String lat, String lng, String closingHours) {
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

    public static OpeningHoursDto createOpeningHoursDto(
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

    public static OpeningHours createOpeningHours(Long id, Place place, DayOfWeek dayOfWeek) {
        OpeningHours openingHours = OpeningHours.of(place, dayOfWeek, LocalTime.now().minusHours(6), LocalTime.now());
        ReflectionTestUtils.setField(openingHours, "id", id);
        return openingHours;
    }

    public static PlaceMenusDto createPlaceMenusDto(Long id, Long placeId, List<String> menus) {
        return PlaceMenusDto.of(id, placeId, menus);
    }

    public static PlaceMenus createPlaceMenus(Long id, Place place, List<String> menus) {
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
