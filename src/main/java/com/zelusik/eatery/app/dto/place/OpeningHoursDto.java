package com.zelusik.eatery.app.dto.place;

import com.zelusik.eatery.app.constant.place.DayOfWeek;
import com.zelusik.eatery.app.domain.place.OpeningHours;

import java.time.LocalDateTime;
import java.time.LocalTime;

public record OpeningHoursDto(
        Long id,
        Long placeId,
        DayOfWeek dayOfWeek,
        LocalTime openAt,
        LocalTime closeAt,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {

    public static OpeningHoursDto of(Long id, Long placeId, DayOfWeek dayOfWeek, LocalTime openAt, LocalTime closedAt, LocalDateTime createdAt, LocalDateTime updatedAt) {
        return new OpeningHoursDto(id, placeId, dayOfWeek, openAt, closedAt, createdAt, updatedAt);
    }

    public static OpeningHoursDto from(OpeningHours entity) {
        return of(
                entity.getId(),
                entity.getPlace().getId(),
                entity.getDayOfWeek(),
                entity.getOpenAt(),
                entity.getCloseAt(),
                entity.getCreatedAt(),
                entity.getUpdatedAt()
        );
    }
}
