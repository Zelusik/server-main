package com.zelusik.eatery.util;

import com.zelusik.eatery.app.domain.constant.DayOfWeek;
import com.zelusik.eatery.app.domain.constant.KakaoCategoryGroupCode;
import com.zelusik.eatery.app.domain.place.Address;
import com.zelusik.eatery.app.domain.place.Place;
import com.zelusik.eatery.app.domain.place.PlaceCategory;
import com.zelusik.eatery.app.domain.place.Point;
import com.zelusik.eatery.app.dto.place.OpeningHoursDto;
import com.zelusik.eatery.app.dto.place.PlaceDto;
import com.zelusik.eatery.app.dto.place.request.PlaceRequest;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.LocalTime;
import java.util.List;

public class PlaceTestUtils {

    public static PlaceRequest createPlaceRequest() {
        return PlaceRequest.of(
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

    public static PlaceDto createPlaceDtoWithIdAndOpeningHours() {
        return PlaceDto.of(
                1L,
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
                null,
                null,
                null
        );
    }

    public static Place createPlace(String homepageUrl, String closingHours) {
        return createPlaceRequest()
                .toDto(homepageUrl, closingHours)
                .toEntity();
    }

    public static Place createPlaceWithId() {
        return createPlaceWithId(null, null);
    }

    public static Place createPlaceWithId(String homepageUrl, String closingHours) {
        Place place = createPlace(homepageUrl, closingHours);
        ReflectionTestUtils.setField(place, "id", 1L);
        return place;
    }

    private static OpeningHoursDto createOpeningHoursDto(
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
}
