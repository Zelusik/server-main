package com.zelusik.eatery.app.util;

import com.zelusik.eatery.app.constant.review.ReviewKeyword;
import org.springframework.core.convert.converter.Converter;

public class ReviewKeywordRequestConverter implements Converter<String, ReviewKeyword> {

    @Override
    public ReviewKeyword convert(String description) {
        return ReviewKeyword.valueOfDescription(description);
    }
}
