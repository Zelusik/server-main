package com.zelusik.eatery.global.kakao.dto;

import com.zelusik.eatery.domain.place.constant.KakaoCategoryGroupCode;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Map;

@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Getter
public class KakaoPlaceInfo {

    private String placeName;
    private Integer distance;
    private String placeUrl;
    private String categoryName;
    private String addressName;
    private String roadAddressName;
    private String id;
    private String phone;
    private KakaoCategoryGroupCode categoryGroupCode;
    private String x;
    private String y;

    public static KakaoPlaceInfo of(String placeName, Integer distance, String placeUrl, String categoryName, String addressName, String roadAddressName, String id, String phone, KakaoCategoryGroupCode categoryGroupCode, String x, String y) {
        return new KakaoPlaceInfo(placeName, distance, placeUrl, categoryName, addressName, roadAddressName, id, phone, categoryGroupCode, x, y);
    }

    public static KakaoPlaceInfo from(Map<String, Object> attributes) {
        String distance = String.valueOf(attributes.get("distance"));

        return new KakaoPlaceInfo(
                String.valueOf(attributes.get("place_name")),
                distance.isEmpty() ? null : Integer.valueOf(distance),
                String.valueOf(attributes.get("place_url")),
                String.valueOf(attributes.get("category_name")),
                String.valueOf(attributes.get("address_name")),
                String.valueOf(attributes.get("road_address_name")),
                String.valueOf(attributes.get("id")),
                String.valueOf(attributes.get("phone")),
                KakaoCategoryGroupCode.valueOf(String.valueOf(attributes.get("category_group_code"))),
                String.valueOf(attributes.get("x")),
                String.valueOf(attributes.get("y"))
        );
    }
}
