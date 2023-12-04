package com.zelusik.eatery.domain.place.entity;

import com.zelusik.eatery.domain.place.constant.KakaoCategoryGroupCode;
import com.zelusik.eatery.domain.review.constant.ReviewKeywordValue;
import com.zelusik.eatery.domain.place.converter.ReviewKeywordValueConverter;
import com.zelusik.eatery.global.common.entity.BaseTimeEntity;
import com.zelusik.eatery.domain.opening_hour.entity.OpeningHour;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;

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
    @Column(nullable = false)
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
    private List<OpeningHour> openingHourList = new LinkedList<>();

    public static Place of(
            @NonNull String kakaoPid,
            @NonNull String name,
            @NonNull String pageUrl,
            @NonNull KakaoCategoryGroupCode categoryGroupCode,
            @NonNull PlaceCategory category,
            @Nullable String phone,
            @NonNull Address address,
            @Nullable String homepageUrl,
            @NonNull Point point,
            @Nullable String closingHours
    ) {
        return of(null, List.of(), kakaoPid, name, pageUrl, categoryGroupCode, category, phone, address, homepageUrl, point, closingHours, null, null);
    }

    public static Place of(
            @Nullable Long id,
            @NonNull List<ReviewKeywordValue> top3Keywords,
            @NonNull String kakaoPid,
            @NonNull String name,
            @NonNull String pageUrl,
            @NonNull KakaoCategoryGroupCode categoryGroupCode,
            @NonNull PlaceCategory category,
            @Nullable String phone,
            @NonNull Address address,
            @Nullable String homepageUrl,
            @NonNull Point point,
            @Nullable String closingHours,
            @Nullable LocalDateTime createdAt,
            @Nullable LocalDateTime updatedAt
    ) {
        return new Place(id, top3Keywords, kakaoPid, name, pageUrl, categoryGroupCode, category, phone, address, homepageUrl, point, closingHours, createdAt, updatedAt);
    }

    private Place(@Nullable Long id, @NonNull List<ReviewKeywordValue> top3Keywords, @NonNull String kakaoPid, @NonNull String name, @NonNull String pageUrl, @NonNull KakaoCategoryGroupCode categoryGroupCode, @NonNull PlaceCategory category, @Nullable String phone, @NonNull Address address, @Nullable String homepageUrl, @NonNull Point point, @Nullable String closingHours, @Nullable LocalDateTime createdAt, @Nullable LocalDateTime updatedAt) {
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
