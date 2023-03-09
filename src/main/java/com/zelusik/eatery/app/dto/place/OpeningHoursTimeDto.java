package com.zelusik.eatery.app.dto.place;

import java.time.LocalTime;

public record OpeningHoursTimeDto(
        LocalTime openAt,
        LocalTime closeAt
) {
}
