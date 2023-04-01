package com.zelusik.eatery.util;

import com.zelusik.eatery.app.constant.place.DayOfWeek;
import com.zelusik.eatery.app.constant.place.KakaoCategoryGroupCode;
import com.zelusik.eatery.app.constant.review.ReviewKeywordValue;
import com.zelusik.eatery.app.domain.place.*;
import com.zelusik.eatery.app.dto.place.OpeningHoursDto;
import com.zelusik.eatery.app.dto.place.PlaceDto;
import com.zelusik.eatery.app.dto.place.PlaceDtoWithImages;
import com.zelusik.eatery.app.dto.place.request.PlaceCreateRequest;
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

    public static PlaceDto createPlaceDtoWithOpeningHours() {
        return PlaceDto.of(
                1L,
                List.of(ReviewKeywordValue.FRESH),
                "308342289",
                "연남토마 본점",
                "http://place.map.kakao.com/308342289",
                KakaoCategoryGroupCode.FD6,
                new PlaceCategory("음식점 > 퓨전요리 > 퓨전일식"),
                "02-332-8064",
                new Address("서울 마포구 연남동 568-26", "서울 마포구 월드컵북로6길 61"),
                "http://place.map.kakao.com/308342289",
                new Point("37.5595073462493", "126.921462488105"),
                null,
                List.of(
                        createOpeningHoursDto(1L, DayOfWeek.MON, LocalTime.of(12, 0), LocalTime.of(18, 0)),
                        createOpeningHoursDto(1L, DayOfWeek.TUE, LocalTime.of(12, 0), LocalTime.of(18, 0)),
                        createOpeningHoursDto(1L, DayOfWeek.WED, LocalTime.of(12, 0), LocalTime.of(18, 0))
                ),
                false,
                null,
                null
        );
    }

    public static PlaceDtoWithImages createPlaceDtoWithImagesAndOpeningHours() {
        return PlaceDtoWithImages.of(
                1L,
                List.of(ReviewKeywordValue.FRESH),
                "308342289",
                "연남토마 본점",
                "http://place.map.kakao.com/308342289",
                KakaoCategoryGroupCode.FD6,
                new PlaceCategory("음식점 > 퓨전요리 > 퓨전일식"),
                "02-332-8064",
                new Address("서울 마포구 연남동 568-26", "서울 마포구 월드컵북로6길 61"),
                "http://place.map.kakao.com/308342289",
                new Point("37.5595073462493", "126.921462488105"),
                null,
                List.of(
                        createOpeningHoursDto(1L, DayOfWeek.MON, LocalTime.of(12, 0), LocalTime.of(18, 0)),
                        createOpeningHoursDto(1L, DayOfWeek.TUE, LocalTime.of(12, 0), LocalTime.of(18, 0)),
                        createOpeningHoursDto(1L, DayOfWeek.WED, LocalTime.of(12, 0), LocalTime.of(18, 0))
                ),
                List.of(),
                false,
                LocalDateTime.of(2023, 1, 1, 0, 0),
                LocalDateTime.of(2023, 1, 1, 0, 0)
        );
    }

    public static Place createPlace(Long id, String kakaoPid) {
        return createPlace(id, kakaoPid, null, "37.5595073462493", "126.921462488105", null);
    }

    public static Place createPlace(Long id, String kakaoPid, String homepageUrl, String closingHours) {
        return createPlace(id, kakaoPid, homepageUrl, "37.5595073462493", "126.921462488105", closingHours);
    }

    public static Place createPlace(Long id, String kakaoPid, String homepageUrl, String lat, String lng, String closingHours) {
        return Place.of(
                id,
                List.of(ReviewKeywordValue.FRESH),
                kakaoPid,
                "연남토마 본점",
                "http://place.map.kakao.com/308342289",
                KakaoCategoryGroupCode.FD6,
                new PlaceCategory("음식점 > 퓨전요리 > 퓨전일식"),
                "02-332-8064",
                new Address("서울 마포구 연남동 568-26", "서울 마포구 월드컵북로6길 61"),
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
                closeAt,
                null,
                null
        );
    }

    public static OpeningHours createOpeningHours(Long id, Place place, DayOfWeek dayOfWeek) {
        OpeningHours openingHours = OpeningHours.of(place, dayOfWeek, LocalTime.now().minusHours(6), LocalTime.now());
        ReflectionTestUtils.setField(openingHours, "id", id);
        return openingHours;
    }
}
