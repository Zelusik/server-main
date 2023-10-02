package com.zelusik.eatery.domain.place.dto;

import com.zelusik.eatery.domain.opening_hour.dto.OpeningHourDto;
import com.zelusik.eatery.domain.place.constant.KakaoCategoryGroupCode;
import com.zelusik.eatery.domain.place.entity.Address;
import com.zelusik.eatery.domain.place.entity.Place;
import com.zelusik.eatery.domain.place.entity.PlaceCategory;
import com.zelusik.eatery.domain.place.entity.Point;
import com.zelusik.eatery.domain.review.constant.ReviewKeywordValue;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.lang.NonNull;

import java.util.List;

@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public class PlaceDto {

    private Long id;
    private List<ReviewKeywordValue> top3Keywords;
    private String kakaoPid;
    private String name;
    private String pageUrl;
    private KakaoCategoryGroupCode categoryGroupCode;
    private PlaceCategory category;
    private String phone;
    private Address address;
    private String homepageUrl;
    private Point point;
    private String closingHours;
    private List<OpeningHourDto> openingHours;

    public PlaceDto(String kakaoPid, String name, String pageUrl, KakaoCategoryGroupCode categoryGroupCode, PlaceCategory category, String phone, Address address, String homepageUrl, Point point, String closingHours) {
        this(null, null, kakaoPid, name, pageUrl, categoryGroupCode, category, phone, address, homepageUrl, point, closingHours, null);
    }

    @NonNull
    public static PlaceDto from(@NonNull Place place) {
        return new PlaceDto(
                place.getId(),
                place.getTop3Keywords(),
                place.getKakaoPid(),
                place.getName(),
                place.getPageUrl(),
                place.getCategoryGroupCode(),
                place.getCategory(),
                place.getPhone(),
                place.getAddress(),
                place.getHomepageUrl(),
                place.getPoint(),
                place.getClosingHours(),
                place.getOpeningHourList().stream()
                        .map(OpeningHourDto::from)
                        .toList()
        );
    }

    @NonNull
    public Place toEntity() {
        return Place.of(
                this.getKakaoPid(),
                this.getName(),
                this.getPageUrl(),
                this.getCategoryGroupCode(),
                this.getCategory(),
                this.getPhone(),
                this.getAddress(),
                this.getHomepageUrl(),
                this.getPoint(),
                this.getClosingHours()
        );
    }
}
