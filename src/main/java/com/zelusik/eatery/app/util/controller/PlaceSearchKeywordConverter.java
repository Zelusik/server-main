package com.zelusik.eatery.app.util.controller;

import com.zelusik.eatery.app.constant.place.PlaceSearchKeyword;
import org.springframework.core.convert.converter.Converter;

public class PlaceSearchKeywordConverter implements Converter<String, PlaceSearchKeyword> {

    @Override
    public PlaceSearchKeyword convert(String source) {
        return PlaceSearchKeyword.valueOf(source);
    }
}
