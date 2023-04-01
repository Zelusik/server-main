package com.zelusik.eatery.app.domain.curation;

import com.zelusik.eatery.app.domain.BaseTimeEntity;
import com.zelusik.eatery.app.domain.place.Place;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.time.LocalDateTime;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@Entity
public class CurationElem extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "curation_elem_id")
    private Long id;

    @JoinColumn(name = "curation_id", nullable = false)
    @ManyToOne(fetch = FetchType.LAZY)
    private Curation curation;

    @JoinColumn(name = "place_id", nullable = false)
    @OneToOne(fetch = FetchType.LAZY)
    private Place place;

    @JoinColumn(name = "image_id", nullable = false)
    @OneToOne(fetch = FetchType.LAZY)
    private CurationElemFile image;

    public static CurationElem of(Curation curation, Place place, CurationElemFile image) {
        return of(null, curation, place, image, null, null);
    }

    public static CurationElem of(Long id, Curation curation, Place place, CurationElemFile image, LocalDateTime createdAt, LocalDateTime updatedAt) {
        return CurationElem.builder()
                .id(id)
                .curation(curation)
                .place(place)
                .image(image)
                .createdAt(createdAt)
                .updatedAt(updatedAt)
                .build();
    }

    @Builder(access = AccessLevel.PRIVATE)
    private CurationElem(Long id, Curation curation, Place place, CurationElemFile image, LocalDateTime createdAt, LocalDateTime updatedAt) {
        super(createdAt, updatedAt);
        this.id = id;
        this.curation = curation;
        this.place = place;
        this.image = image;
    }
}
