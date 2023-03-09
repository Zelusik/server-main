package com.zelusik.eatery.app.domain.constant;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

@AllArgsConstructor
@Getter
public enum DayOfWeek {

    MON("월"),
    TUE("화"),
    WED("수"),
    THU("목"),
    FRI("금"),
    SAT("토"),
    SUN("일");

    private final String description;

    public static DayOfWeek valueOfDescription(char description) {
        return valueOfDescription(String.valueOf(description));
    }

    public static DayOfWeek valueOfDescription(String description) {
        return Arrays.stream(values())
                .filter(value -> value.getDescription().equals(description))
                .findFirst()
                .orElseThrow(IllegalArgumentException::new);
    }

    public static List<DayOfWeek> getValuesInRange(DayOfWeek start, DayOfWeek end) {
        List<DayOfWeek> result = new LinkedList<>();
        boolean continueAdding = false;

        for (DayOfWeek dayOfWeek : values()) {
            if (dayOfWeek == start) continueAdding = true;
            if (continueAdding) result.add(dayOfWeek);
            if (dayOfWeek == end) break;
        }

        return result;
    }
}
