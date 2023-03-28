package com.zelusik.eatery.app.dto.place;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalTime;

@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Getter
public class OpeningHoursTimeDto {
    private LocalTime openAt;
    private LocalTime closeAt;

    public static OpeningHoursTimeDto of(LocalTime openAt, LocalTime closeAt) {
        return new OpeningHoursTimeDto(openAt, closeAt);
    }
}
