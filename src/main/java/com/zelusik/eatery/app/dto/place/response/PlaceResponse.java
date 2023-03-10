package com.zelusik.eatery.app.dto.place.response;

import com.zelusik.eatery.app.domain.place.Address;
import com.zelusik.eatery.app.domain.place.PlaceCategory;
import com.zelusik.eatery.app.domain.place.Point;
import com.zelusik.eatery.app.dto.place.PlaceDto;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Getter
public class PlaceResponse {

    @Schema(description = "장소의 id(PK)", example = "1")
    private Long id;

    @Schema(description = "이름", example = "연남토마 본점")
    private String name;

    @Schema(description = "카테고리")
    private PlaceCategory category;

    @Schema(description = "대표번호", example = "02-332-8064")
    private String phone;

    @Schema(description = "주소")
    private Address address;

    @Schema(description = "인스타그램 url", example = "www.instagram.com/toma_wv/")
    private String snsUrl;

    @Schema(description = "좌표")
    private Point point;

    @Schema(description = "휴무일", example = "금요일")
    private String closingHours;

    @Schema(description = "영업시간 정보")
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
