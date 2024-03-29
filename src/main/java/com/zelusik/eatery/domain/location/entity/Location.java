package com.zelusik.eatery.domain.location.entity;

import com.zelusik.eatery.domain.place.entity.Point;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@Table(indexes = {
        @Index(name = "idx__location__sido", columnList = "sido"),
        @Index(name = "idx__location__sgg", columnList = "sgg"),
        @Index(name = "idx__location__emdg", columnList = "emdg")
})
@Entity
public class Location {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "location_id")
    private Long id;

    @Column(nullable = false)
    private String sido;

    private String sgg;

    private String emdg;

    @Embedded
    private Point point;

    public static Location of(String sido, String sgg, String emdg, Point point) {
        return of(null, sido, sgg, emdg, point);
    }

    public static Location of(Long id, String sido, String sgg, String emdg, Point point) {
        return Location.builder()
                .id(id)
                .sido(sido)
                .sgg(sgg)
                .emdg(emdg)
                .point(point)
                .build();
    }

    @Builder(access = AccessLevel.PRIVATE)
    private Location(Long id, String sido, String sgg, String emdg, Point point) {
        this.id = id;
        this.sido = sido;
        this.sgg = sgg;
        this.emdg = emdg;
        this.point = point;
    }
}
