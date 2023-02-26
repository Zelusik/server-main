package com.zelusik.eatery.app.domain.constant;

public enum Gender {

    MALE, FEMALE;

    public static Gender caseFreeValueOf(String name) {
        return name.equalsIgnoreCase("male") ? MALE : FEMALE;
    }
}
