package com.zelusik.eatery.domain.location.dto;

import com.zelusik.eatery.domain.location.entity.Location;
import com.zelusik.eatery.domain.place.entity.Point;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Getter
public class LocationDto {

    private Long id;
    private String sido;
    private String sgg;
    private String emdg;
    private Point point;

    public static LocationDto of(String sido, String sgg, String emdg, Point point) {
        return of(null, sido, sgg, emdg, point);
    }

    public static LocationDto of(Long id, String sido, String sgg, String emdg, Point point) {
        return new LocationDto(id, sido, sgg, emdg, point);
    }

    public static LocationDto from(Location entity) {
        return of(
                entity.getId(),
                entity.getSido(),
                entity.getSgg(),
                entity.getEmdg(),
                entity.getPoint()
        );
    }
}
