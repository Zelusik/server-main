package com.zelusik.eatery.domain.place;

import com.zelusik.eatery.domain.BaseEntity;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.time.LocalDateTime;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@Table(indexes = {
        @Index(name = "idx__place_menu__place", columnList = "place_id")
})
@Entity
public class PlaceMenu extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "place_menu_id")
    private Long id;

    @JoinColumn(name = "place_id")
    @ManyToOne(fetch = FetchType.LAZY)
    private Place place;

    private String name;

    public static PlaceMenu of(Place place, String name) {
        return of(null, place, name, null, null, null, null);
    }

    public static PlaceMenu of(Long id, Place place, String name, LocalDateTime createdAt, LocalDateTime updatedAt, Long createdBy, Long updatedBy) {
        return new PlaceMenu(id, place, name, createdAt, updatedAt, createdBy, updatedBy);
    }

    private PlaceMenu(Long id, Place place, String name, LocalDateTime createdAt, LocalDateTime updatedAt, Long createdBy, Long updatedBy) {
        super(createdAt, updatedAt, createdBy, updatedBy);
        this.id = id;
        this.place = place;
        this.name = name;
    }
}
