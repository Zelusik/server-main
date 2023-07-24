package com.zelusik.eatery.domain.place;

import com.zelusik.eatery.constant.place.KakaoCategoryGroupCode;
import com.zelusik.eatery.constant.review.ReviewKeywordValue;
import com.zelusik.eatery.converter.ReviewKeywordValueConverter;
import com.zelusik.eatery.domain.BaseTimeEntity;
import lombok.*;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.LinkedList;
import java.util.List;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@Table(indexes = {
        @Index(name = "idx__place__kakao_pid", columnList = "kakaoPid"),
        @Index(name = "idx__place__created_at", columnList = "createdAt")
})
@Entity
public class Place extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "place_id")
    private Long id;

    @Setter
    @Convert(converter = ReviewKeywordValueConverter.class)
    private List<ReviewKeywordValue> top3Keywords;

    @Column(unique = true, nullable = false)
    private String kakaoPid;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private String pageUrl;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private KakaoCategoryGroupCode categoryGroupCode;

    @Embedded
    private PlaceCategory category;

    private String phone;

    @Embedded
    private Address address;

    private String homepageUrl;

    @Embedded
    private Point point;

    private String closingHours;

    @OneToMany(mappedBy = "place")
    private List<OpeningHours> openingHoursList = new LinkedList<>();

    public static Place of(String kakaoPid, String name, String pageUrl, KakaoCategoryGroupCode categoryGroupCode, PlaceCategory category, String phone, Address address, String homepageUrl, Point point, String closingHours) {
        return of(null, null, kakaoPid, name, pageUrl, categoryGroupCode, category, phone, address, homepageUrl, point, closingHours, null, null);
    }

    public static Place of(Long id, List<ReviewKeywordValue> top3Keywords, String kakaoPid, String name, String pageUrl, KakaoCategoryGroupCode categoryGroupCode, PlaceCategory category, String phone, Address address, String homepageUrl, Point point, String closingHours, LocalDateTime createdAt, LocalDateTime updatedAt) {
        return Place.builder()
                .id(id)
                .top3Keywords(top3Keywords)
                .kakaoPid(kakaoPid)
                .name(name)
                .pageUrl(pageUrl)
                .categoryGroupCode(categoryGroupCode)
                .category(category)
                .phone(phone)
                .address(address)
                .homepageUrl(homepageUrl)
                .point(point)
                .closingHours(closingHours)
                .createdAt(createdAt)
                .updatedAt(updatedAt)
                .build();
    }

    @Builder(access = AccessLevel.PRIVATE)
    private Place(Long id, List<ReviewKeywordValue> top3Keywords, String kakaoPid, String name, String pageUrl, KakaoCategoryGroupCode categoryGroupCode, PlaceCategory category, String phone, Address address, String homepageUrl, Point point, String closingHours, LocalDateTime createdAt, LocalDateTime updatedAt) {
        super(createdAt, updatedAt);
        this.id = id;
        this.top3Keywords = top3Keywords;
        this.kakaoPid = kakaoPid;
        this.name = name;
        this.pageUrl = pageUrl;
        this.categoryGroupCode = categoryGroupCode;
        this.category = category;
        this.phone = phone;
        this.address = address;
        this.homepageUrl = homepageUrl;
        this.point = point;
        this.closingHours = closingHours;
    }
}
