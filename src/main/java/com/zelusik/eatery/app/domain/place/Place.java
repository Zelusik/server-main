package com.zelusik.eatery.app.domain.place;

import com.zelusik.eatery.app.domain.BaseTimeEntity;
import com.zelusik.eatery.app.domain.constant.KakaoCategoryGroupCode;
import lombok.*;
import org.hibernate.annotations.SQLDelete;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.LinkedList;
import java.util.List;

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

    private LocalDateTime deletedAt;

    public static Place of(String kakaoPid, String name, String pageUrl, KakaoCategoryGroupCode categoryGroupCode, PlaceCategory category, String phone, Address address, String homepageUrl, Point point, String closingHours) {
        return of(null, kakaoPid, name, pageUrl, categoryGroupCode, category, phone, address, homepageUrl, point, closingHours, null, null, null);
    }

    public static Place of(Long id, String kakaoPid, String name, String pageUrl, KakaoCategoryGroupCode categoryGroupCode, PlaceCategory category, String phone, Address address, String homepageUrl, Point point, String closingHours, LocalDateTime createdAt, LocalDateTime updatedAt, LocalDateTime deletedAt) {
        return Place.builder()
                .id(id)
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
                .deletedAt(deletedAt)
                .build();
    }

    @Builder(access = AccessLevel.PRIVATE)
    private Place(LocalDateTime createdAt, LocalDateTime updatedAt, Long id, String kakaoPid, String name, String pageUrl, KakaoCategoryGroupCode categoryGroupCode, PlaceCategory category, String phone, Address address, String homepageUrl, Point point, String closingHours, LocalDateTime deletedAt) {
        super(createdAt, updatedAt);
        this.id = id;
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
        this.deletedAt = deletedAt;
    }
}
