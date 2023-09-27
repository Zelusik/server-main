package com.zelusik.eatery.domain.opening_hours.dto;

import com.zelusik.eatery.domain.place.constant.DayOfWeek;
import com.zelusik.eatery.domain.opening_hours.entity.OpeningHours;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalTime;

@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Getter
public class OpeningHoursDto {

    private Long id;
    private Long placeId;
    private DayOfWeek dayOfWeek;
    private LocalTime openAt;
    private LocalTime closeAt;

    public static OpeningHoursDto of(Long id, Long placeId, DayOfWeek dayOfWeek, LocalTime openAt, LocalTime closedAt) {
        return new OpeningHoursDto(id, placeId, dayOfWeek, openAt, closedAt);
    }

    public static OpeningHoursDto from(OpeningHours entity) {
        return of(
                entity.getId(),
                entity.getPlace().getId(),
                entity.getDayOfWeek(),
                entity.getOpenAt(),
                entity.getCloseAt()
        );
    }
}
