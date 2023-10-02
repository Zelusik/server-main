package com.zelusik.eatery.domain.place.dto;

import com.zelusik.eatery.domain.opening_hour.dto.OpeningHourDto;
import com.zelusik.eatery.domain.place.constant.KakaoCategoryGroupCode;
import com.zelusik.eatery.domain.place.entity.Address;
import com.zelusik.eatery.domain.place.entity.Place;
import com.zelusik.eatery.domain.place.entity.PlaceCategory;
import com.zelusik.eatery.domain.place.entity.Point;
import com.zelusik.eatery.domain.review.constant.ReviewKeywordValue;
import com.zelusik.eatery.domain.review_image.dto.ReviewImageDto;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
@Getter
public class PlaceWithMarkedStatusAndImagesDto extends PlaceDto {

    private Boolean isMarked;
    private List<ReviewImageDto> images;

    public PlaceWithMarkedStatusAndImagesDto(Long id, List<ReviewKeywordValue> top3Keywords, String kakaoPid, String name, String pageUrl, KakaoCategoryGroupCode categoryGroupCode, PlaceCategory category, String phone, Address address, String homepageUrl, Point point, String closingHours, List<OpeningHourDto> openingHours, Boolean isMarked, List<ReviewImageDto> images) {
        super(id, top3Keywords, kakaoPid, name, pageUrl, categoryGroupCode, category, phone, address, homepageUrl, point, closingHours, openingHours);
        this.isMarked = isMarked;
        this.images = images;
    }

    public static PlaceWithMarkedStatusAndImagesDto from(Place place, boolean isMarked, List<ReviewImageDto> images) {
        return new PlaceWithMarkedStatusAndImagesDto(
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
                        .toList(),
                isMarked,
                images);
    }
}
