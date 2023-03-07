package com.zelusik.eatery.app.dto.place;

import com.zelusik.eatery.app.domain.place.OpeningHours;

import java.time.DayOfWeek;
import java.time.LocalTime;

public record OpeningHoursDto(
        Long id,
        Long placeId,
        DayOfWeek day,
        LocalTime openAt,
        LocalTime closedAt
) {

    public static OpeningHoursDto of(Long id, Long placeId, DayOfWeek day, LocalTime openAt, LocalTime closedAt) {
        return new OpeningHoursDto(id, placeId, day, openAt, closedAt);
    }

    public static OpeningHoursDto from(OpeningHours entity) {
        return of(
                entity.getId(),
                entity.getPlace().getId(),
                entity.getDay(),
                entity.getOpenAt(),
                entity.getClosedAt()
        );
    }
}
