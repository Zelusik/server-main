package com.zelusik.eatery.app.dto.place.response;

import com.zelusik.eatery.app.dto.place.OpeningHoursDto;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalTime;

@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Getter
public class OpeningHoursResponse {

    private Long id;
    private String dayOfWeek;
    private String openAt;
    private String closeAt;

    public static OpeningHoursResponse from(OpeningHoursDto openingHoursDto) {
        return new OpeningHoursResponse(
                openingHoursDto.id(),
                openingHoursDto.dayOfWeek().getDescription(),
                parseTimeResponseOf(openingHoursDto.openAt()),
                parseTimeResponseOf(openingHoursDto.closeAt())
        );
    }

    /**
     * 12:30:00 => 12:30
     *
     * @param time
     * @return
     */
    private static String parseTimeResponseOf(LocalTime time) {
        return time.toString().substring(0, 5);
    }
}
