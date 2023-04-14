package com.zelusik.eatery.domain.place;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.Column;
import javax.persistence.Embeddable;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Getter
@Embeddable
public class Point {

    @Schema(description = "위도", example = "37.5595073462493")
    @Column(nullable = false)
    private String lat;

    @Schema(description = "경도", example = "126.921462488105")
    @Column(nullable = false)
    private String lng;
}
