package com.zelusik.eatery.domain.opening_hour.dto;

import com.zelusik.eatery.domain.opening_hour.entity.OpeningHour;
import com.zelusik.eatery.domain.place.constant.DayOfWeek;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalTime;

@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Getter
public class OpeningHourDto {

    private Long id;
    private Long placeId;
    private DayOfWeek dayOfWeek;
    private LocalTime openAt;
    private LocalTime closeAt;

    public static OpeningHourDto of(Long id, Long placeId, DayOfWeek dayOfWeek, LocalTime openAt, LocalTime closedAt) {
        return new OpeningHourDto(id, placeId, dayOfWeek, openAt, closedAt);
    }

    public static OpeningHourDto from(OpeningHour entity) {
        return of(
                entity.getId(),
                entity.getPlace().getId(),
                entity.getDayOfWeek(),
                entity.getOpenAt(),
                entity.getCloseAt()
        );
    }
}
