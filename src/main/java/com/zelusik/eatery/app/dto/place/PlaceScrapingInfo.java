package com.zelusik.eatery.app.dto.place;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Map;

@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Getter
public class PlaceScrapingInfo {

    private String openingHours;
    private String closingHours;
    private String homepageUrl;

    public static PlaceScrapingInfo of(String openingHours, String closingHours, String homepageUrl) {
        return new PlaceScrapingInfo(openingHours, closingHours, homepageUrl);
    }

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
