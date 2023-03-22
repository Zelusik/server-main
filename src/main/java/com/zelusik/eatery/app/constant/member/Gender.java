package com.zelusik.eatery.app.constant.member;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Arrays;

@Schema(
        description = "<p>성별. 목록은 다음과 같습니다." +
                "<ul>" +
                "<li>MALE</li>" +
                "<li>FEMALE</li>" +
                "<li>ETC</li>" +
                "</ul>"
)
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
