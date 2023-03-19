package com.zelusik.eatery.app.domain.curation;

import com.zelusik.eatery.app.domain.BaseTimeEntity;
import com.zelusik.eatery.app.domain.place.Place;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.SQLDelete;

import javax.persistence.*;
import java.time.LocalDateTime;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@SQLDelete(sql = "UPDATE curation_elem SET deleted_at = CURRENT_TIMESTAMP WHERE curation_elem_id = ?")
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

    private LocalDateTime deletedAt;
}
