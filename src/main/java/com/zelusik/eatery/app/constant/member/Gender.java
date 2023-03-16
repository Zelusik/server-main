package com.zelusik.eatery.app.constant.member;

public enum Gender {

    MALE, FEMALE;

    public static Gender caseFreeValueOf(String name) {
        if (name.equalsIgnoreCase("male")) {
            return MALE;
        } else if (name.equalsIgnoreCase("female")) {
            return FEMALE;
        }
        return null;
    }
}
