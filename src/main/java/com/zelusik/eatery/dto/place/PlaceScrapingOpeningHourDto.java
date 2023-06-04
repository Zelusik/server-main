package com.zelusik.eatery.dto.place;

import com.zelusik.eatery.constant.place.DayOfWeek;
import com.zelusik.eatery.domain.place.OpeningHours;
import com.zelusik.eatery.domain.place.Place;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalTime;
import java.util.Map;

@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Getter
public class PlaceScrapingOpeningHourDto {

    private DayOfWeek day;
    private LocalTime openAt;
    private LocalTime closeAt;

    public static PlaceScrapingOpeningHourDto of(DayOfWeek day, LocalTime openAt, LocalTime closeAt) {
        return new PlaceScrapingOpeningHourDto(day, openAt, closeAt);
    }

    public static PlaceScrapingOpeningHourDto from(Map<String, Object> attributes) {
        return of(
                DayOfWeek.valueOf(String.valueOf(attributes.get("day"))),
                LocalTime.parse(String.valueOf(attributes.get("openAt"))),
                LocalTime.parse(String.valueOf(attributes.get("closeAt")))
        );
    }

    public OpeningHours toOpeningHoursEntity(Place place) {
        return OpeningHours.of(place, day, openAt, closeAt);
    }
}
