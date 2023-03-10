package com.zelusik.eatery.app.dto.place;

import java.util.Map;

public record PlaceScrapingInfo(
        String openingHours,
        String closingHours,
        String homepageUrl
) {

    public static PlaceScrapingInfo from(Map<String, Object> attributes) {
        Object openingSchedule = attributes.get("opening_hours");
        Object closingSchedule = attributes.get("closing_hours");
        Object homepageUrl = attributes.get("homepage_url");

        return new PlaceScrapingInfo(
                openingSchedule == null ? null : openingSchedule.toString(),
                closingSchedule == null ? null : closingSchedule.toString(),
                homepageUrl == null ? null : homepageUrl.toString()
        );
    }
}
