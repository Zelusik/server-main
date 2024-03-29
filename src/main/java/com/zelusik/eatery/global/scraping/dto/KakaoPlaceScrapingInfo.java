package com.zelusik.eatery.global.scraping.dto;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.lang.Nullable;

import java.util.List;
import java.util.Map;

@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Getter
public class KakaoPlaceScrapingInfo {

    @Nullable
    private List<PlaceScrapingOpeningHourDto> openingHours;

    @Nullable
    private String closingHours;

    @Nullable
    private String homepageUrl;

    public static KakaoPlaceScrapingInfo of(List<PlaceScrapingOpeningHourDto> openingHours, String closingHours, String homepageUrl) {
        return new KakaoPlaceScrapingInfo(openingHours, closingHours, homepageUrl);
    }

    @SuppressWarnings("unchecked")
    // TODO: Object => List<Map> 변환 로직이 있어서 generic type casting 문제를 무시한다. 더 좋은 방법이 있다면 고려할 수 있음.
    public static KakaoPlaceScrapingInfo from(Map<String, Object> attributes) {
        List<Map<String, Object>> openingHours = (List<Map<String, Object>>) attributes.get("openingHours");
        Object closingHours = attributes.get("closingHours");
        Object homepageUrl = attributes.get("homepageUrl");
        return of(
                openingHours != null ? openingHours.stream().map(PlaceScrapingOpeningHourDto::from).toList() : null,
                closingHours != null ? closingHours.toString() : null,
                homepageUrl != null ? homepageUrl.toString() : null
        );
    }
}
