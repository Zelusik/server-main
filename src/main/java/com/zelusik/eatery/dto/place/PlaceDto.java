package com.zelusik.eatery.dto.place;

import com.zelusik.eatery.constant.place.KakaoCategoryGroupCode;
import com.zelusik.eatery.constant.review.ReviewKeywordValue;
import com.zelusik.eatery.domain.place.Address;
import com.zelusik.eatery.domain.place.Place;
import com.zelusik.eatery.domain.place.PlaceCategory;
import com.zelusik.eatery.domain.place.Point;
import com.zelusik.eatery.dto.review.ReviewImageDto;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;

import java.time.LocalDateTime;
import java.util.List;

@AllArgsConstructor(access = AccessLevel.PRIVATE)
@NoArgsConstructor(access = AccessLevel.PRIVATE)
@Getter
public class PlaceDto {

    private Long id;

    private List<ReviewKeywordValue> top3Keywords;

    private String kakaoPid;

    private String name;

    private String pageUrl;

    private KakaoCategoryGroupCode categoryGroupCode;

    private PlaceCategory category;

    @Nullable
    private String phone;

    private Address address;

    @Nullable
    private String homepageUrl;

    private Point point;

    @Nullable
    private String closingHours;

    private List<OpeningHoursDto> openingHoursDtos;

    @Nullable
    private List<ReviewImageDto> images;

    @Nullable
    private Boolean isMarked;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

    @NonNull
    public static PlaceDto of(String kakaoPid, String name, String pageUrl, KakaoCategoryGroupCode categoryGroupCode, PlaceCategory category, String phone, Address address, String homepageUrl, Point point, String closingHours) {
        return of(null, null, kakaoPid, name, pageUrl, categoryGroupCode, category, phone, address, homepageUrl, point, closingHours, null, null, null, null, null);
    }

    @NonNull
    public static PlaceDto of(Long id, List<ReviewKeywordValue> top3Keywords, String kakaoPid, String name, String pageUrl, KakaoCategoryGroupCode categoryGroupCode, PlaceCategory category, String phone, Address address, String homepageUrl, Point point, String closingHours, List<OpeningHoursDto> openingHoursDtos, List<ReviewImageDto> reviewImageDtos, Boolean isMarked, LocalDateTime createdAt, LocalDateTime updatedAt) {
        return new PlaceDto(id, top3Keywords, kakaoPid, name, pageUrl, categoryGroupCode, category, phone, address, homepageUrl, point, closingHours, openingHoursDtos, reviewImageDtos, isMarked, createdAt, updatedAt);
    }

    @NonNull
    public static PlaceDto from(@NonNull Place place, @Nullable Boolean isMarked) {
        return fromWithImages(place, isMarked, null);
    }

    @NonNull
    public static PlaceDto fromWithImages(@NonNull Place place, @Nullable Boolean isMarked, @Nullable List<ReviewImageDto> images) {
        return of(
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
                place.getOpeningHoursList().stream()
                        .map(OpeningHoursDto::from)
                        .toList(),
                images,
                isMarked,
                place.getCreatedAt(),
                place.getUpdatedAt()
        );
    }

    @NonNull
    public static PlaceDto fromWithoutMarkedStatusAndImages(@NonNull Place place) {
        return from(place, null);
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
