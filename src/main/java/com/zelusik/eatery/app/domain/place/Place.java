package com.zelusik.eatery.app.domain.place;

import com.zelusik.eatery.app.domain.BaseTimeEntity;
import com.zelusik.eatery.app.domain.constant.KakaoCategoryGroupCode;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.SQLDelete;

import javax.persistence.*;
import java.time.LocalDateTime;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@SQLDelete(sql = "UPDATE place SET deleted_at = CURRENT_TIMESTAMP WHERE place_id = ?")
@Entity
public class Place extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "place_id")
    private Long id;

    @Column(nullable = false)
    private String kakaoPid;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private String pageUrl;

    @Column(nullable = false)
    private KakaoCategoryGroupCode categoryGroupCode;

    @Embedded
    private PlaceCategory category;

    private String phone;

    @Embedded
    private Address address;

    private String snsUrl;

    @Embedded
    private Point point;

    private LocalDateTime deletedAt;

    public static Place of(String kakaoPid, String name, String pageUrl, KakaoCategoryGroupCode categoryGroupCode, PlaceCategory category, String phone, Address address, String snsUrl, Point point) {
        return new Place(kakaoPid, name, pageUrl, categoryGroupCode, category, phone, address, snsUrl, point);
    }

    private Place(String kakaoPid, String name, String pageUrl, KakaoCategoryGroupCode categoryGroupCode, PlaceCategory category, String phone, Address address, String snsUrl, Point point) {
        this.kakaoPid = kakaoPid;
        this.name = name;
        this.pageUrl = pageUrl;
        this.categoryGroupCode = categoryGroupCode;
        this.category = category;
        this.phone = phone;
        this.address = address;
        this.snsUrl = snsUrl;
        this.point = point;
    }
}
