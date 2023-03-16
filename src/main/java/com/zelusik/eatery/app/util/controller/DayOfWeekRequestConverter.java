package com.zelusik.eatery.app.util.controller;

import com.zelusik.eatery.app.constant.place.DayOfWeek;
import org.springframework.core.convert.converter.Converter;

public class DayOfWeekRequestConverter implements Converter<String, DayOfWeek> {

    @Override
    public DayOfWeek convert(String source) {
        return DayOfWeek.valueOfDescription(source);
    }
}
