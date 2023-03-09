package com.zelusik.eatery.app.dto.place.response;

import com.zelusik.eatery.app.domain.place.Address;
import com.zelusik.eatery.app.domain.place.PlaceCategory;
import com.zelusik.eatery.app.domain.place.Point;
import com.zelusik.eatery.app.dto.place.PlaceDto;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Getter
public class PlaceResponse {

    private Long id;
    private String name;
    private PlaceCategory category;
    private String phone;
    private Address address;
    private String snsUrl;
    private Point point;
    private String closingHours;
    private List<OpeningHoursResponse> openingHours;

    public static PlaceResponse of(Long id, String name, PlaceCategory category, String phone, Address address, String snsUrl, Point point, String closingHours, List<OpeningHoursResponse> openingHours) {
        return new PlaceResponse(id, name, category, phone, address, snsUrl, point, closingHours, openingHours);
    }

    public static PlaceResponse from(PlaceDto placeDto) {
        String snsUrl = placeDto.homepageUrl();
        if (snsUrl != null && !snsUrl.contains("instagram")) {
            snsUrl = null;
        }

        return new PlaceResponse(
                placeDto.id(),
                placeDto.name(),
                placeDto.category(),
                placeDto.phone(),
                placeDto.address(),
                snsUrl,
                placeDto.point(),
                placeDto.closingHours(),
                placeDto.openingHoursDtos().stream()
                        .map(OpeningHoursResponse::from)
                        .toList()
        );
    }
}
