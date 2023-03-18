package com.zelusik.eatery.app.constant.member;

import com.zelusik.eatery.global.exception.place.NotAcceptableFoodCategory;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Arrays;

@AllArgsConstructor
@Getter
public enum Gender {

    MALE("남성"),
    FEMALE("여성"),
    ETC("기타"),
    ;

    private final String description;

    public static Gender valueOfDescription(String description) {
        return Arrays.stream(values())
                .filter(value -> description.equals(value.getDescription()))
                .findFirst()
                .orElseThrow(IllegalArgumentException::new);
    }

    public static Gender caseFreeValueOf(String name) {
        if (name.equalsIgnoreCase("male")) {
            return MALE;
        } else if (name.equalsIgnoreCase("female")) {
            return FEMALE;
        }
        return null;
    }
}
