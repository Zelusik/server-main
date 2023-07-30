package com.zelusik.eatery.domain.place;

import com.zelusik.eatery.converter.PlaceMenusConverter;
import com.zelusik.eatery.domain.BaseEntity;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.time.LocalDateTime;
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

    @Convert(converter = PlaceMenusConverter.class)
    private List<String> menus;

    public static PlaceMenus of(Place place, List<String> menus) {
        return of(null, place, menus, null, null, null, null);
    }

    public static PlaceMenus of(Long id, Place place, List<String> menus, LocalDateTime createdAt, LocalDateTime updatedAt, Long createdBy, Long updatedBy) {
        return new PlaceMenus(id, place, menus, createdAt, updatedAt, createdBy, updatedBy);
    }

    public PlaceMenus(Long id, Place place, List<String> menus, LocalDateTime createdAt, LocalDateTime updatedAt, Long createdBy, Long updatedBy) {
        super(createdAt, updatedAt, createdBy, updatedBy);
        this.id = id;
        this.place = place;
        this.menus = menus;
    }

    public void updateMenus(List<String> menus) {
        this.menus = menus;
    }
}
