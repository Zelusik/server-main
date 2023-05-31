package com.zelusik.eatery.dto.place.request;

import com.zelusik.eatery.constant.place.KakaoCategoryGroupCode;
import com.zelusik.eatery.domain.place.Address;
import com.zelusik.eatery.domain.place.PlaceCategory;
import com.zelusik.eatery.domain.place.Point;
import com.zelusik.eatery.dto.place.PlaceDtoWithMarkedStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@AllArgsConstructor(access = AccessLevel.PRIVATE)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Setter
@Getter
public class PlaceCreateRequest {

    @Schema(description = "카카오에서 응답한 장소 id", example = "308342289")
    @NotBlank
    private String kakaoPid;

    @Schema(description = "장소 이름", example = "연남토마 본점")
    @NotBlank
    private String name;

    @Schema(description = "상세 페이지 주소", example = "http://place.map.kakao.com/308342289")
    @NotBlank
    private String pageUrl;

    @Schema(description = "카카오에서 응답한 카테고리 그룹 코드(<code>category_group_code</code>)", example = "FD6")
    @NotNull
    private KakaoCategoryGroupCode categoryGroupCode;

    @Schema(description = "카카에에서 응답한 카테고리 이름(<code>category_name</code>)", example = "음식점 > 퓨전요리 > 퓨전일식")
    @NotBlank
    private String categoryName;

    @Schema(description = "대표 전화번호", example = "02-332-8064")
    private String phone;

    @Schema(description = "지번 주소", example = "서울 마포구 연남동 568-26")
    private String lotNumberAddress;

    @Schema(description = "도로명 주소", example = "서울 마포구 월드컵북로6길 61")
    private String roadAddress;

    @Schema(description = "위도", example = "37.5595073462493")
    @NotBlank
    private String lat;

    @Schema(description = "경도", example = "126.921462488105")
    @NotBlank
    private String lng;

    public static PlaceCreateRequest of(String kakaoPid, String name, String pageUrl, KakaoCategoryGroupCode categoryGroupCode, String categoryName, String phone, String lotNumberAddress, String roadAddress, String lat, String lng) {
        return new PlaceCreateRequest(kakaoPid, name, pageUrl, categoryGroupCode, categoryName, phone, lotNumberAddress, roadAddress, lat, lng);
    }

    public PlaceDtoWithMarkedStatus toDto(String homepageUrl, String closingHours) {
        return PlaceDtoWithMarkedStatus.of(
                this.getKakaoPid(),
                this.getName(),
                this.getPageUrl(),
                this.getCategoryGroupCode(),
                new PlaceCategory(this.getCategoryName()),
                this.getPhone(),
                new Address(this.getLotNumberAddress(), this.getRoadAddress()),
                homepageUrl,
                new Point(this.getLat(), this.getLng()),
                closingHours
        );
    }
}
