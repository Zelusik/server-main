package com.zelusik.eatery.domain.place_menus.entity;

import com.zelusik.eatery.domain.place_menus.converter.PlaceMenusConverter;
import com.zelusik.eatery.global.common.entity.BaseEntity;
import com.zelusik.eatery.domain.place.entity.Place;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@Table(indexes = {
        @Index(name = "idx__place_menus__place", columnList = "place_id")
})
@Entity
public class PlaceMenus extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "place_menus_id")
    private Long id;

    @JoinColumn(name = "place_id", nullable = false, unique = true)
    @OneToOne(fetch = FetchType.LAZY)
    private Place place;

    @Column(length = 10000)
    @Convert(converter = PlaceMenusConverter.class)
    private List<String> menus;

    public static PlaceMenus of(@NonNull Place place, @NonNull List<String> menus) {
        return of(null, place, menus, null, null, null, null);
    }

    public static PlaceMenus of(
            @Nullable Long id,
            @NonNull Place place,
            @NonNull List<String> menus,
            @Nullable LocalDateTime createdAt,
            @Nullable LocalDateTime updatedAt,
            @Nullable Long createdBy,
            @Nullable Long updatedBy
    ) {
        return new PlaceMenus(id, place, menus, createdAt, updatedAt, createdBy, updatedBy);
    }

    private PlaceMenus(@Nullable Long id, @NonNull Place place, @NonNull List<String> menus, @Nullable LocalDateTime createdAt, @Nullable LocalDateTime updatedAt, @Nullable Long createdBy, @Nullable Long updatedBy) {
        super(createdAt, updatedAt, createdBy, updatedBy);
        this.id = id;
        this.place = place;
        this.menus = menus;
    }

    public void updateMenus(List<String> menus) {
        this.menus = menus;
    }

    public void addMenu(String menu) {
        List<String> newMenus = new ArrayList<>(this.getMenus());
        newMenus.add(menu);
        this.menus = newMenus;
    }
}
