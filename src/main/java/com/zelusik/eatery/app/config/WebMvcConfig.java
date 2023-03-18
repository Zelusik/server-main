package com.zelusik.eatery.app.config;

import com.zelusik.eatery.app.util.controller.DayOfWeekRequestConverter;
import com.zelusik.eatery.app.util.controller.PlaceSearchKeywordConverter;
import com.zelusik.eatery.app.util.controller.ReviewKeywordRequestConverter;
import org.springframework.context.annotation.Configuration;
import org.springframework.format.FormatterRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebMvcConfig implements WebMvcConfigurer {

    @Override
    public void addFormatters(FormatterRegistry registry) {
        registry.addConverter(new ReviewKeywordRequestConverter());
        registry.addConverter(new DayOfWeekRequestConverter());
        registry.addConverter(new PlaceSearchKeywordConverter());
    }
}
