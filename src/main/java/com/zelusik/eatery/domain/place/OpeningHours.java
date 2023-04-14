package com.zelusik.eatery.domain.place;

import com.zelusik.eatery.constant.place.DayOfWeek;
import com.zelusik.eatery.domain.BaseTimeEntity;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.time.LocalTime;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@Table(indexes = {
        @Index(columnList = "dayOfWeek")
})
@Entity
public class OpeningHours extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "opening_hour_id")
    private Long id;

    @Setter
    @JoinColumn(name = "place_id")
    @ManyToOne(fetch = FetchType.LAZY)
    private Place place;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private DayOfWeek dayOfWeek;

    @Column(nullable = false)
    private LocalTime openAt;

    @Column(nullable = false)
    private LocalTime closeAt;

    public static OpeningHours of(Place place, DayOfWeek dayOfWeek, LocalTime openAt, LocalTime closeAt) {
        return new OpeningHours(place, dayOfWeek, openAt, closeAt);
    }

    private OpeningHours(Place place, DayOfWeek dayOfWeek, LocalTime openAt, LocalTime closeAt) {
        this.place = place;
        this.dayOfWeek = dayOfWeek;
        this.openAt = openAt;
        this.closeAt = closeAt;
    }
}
