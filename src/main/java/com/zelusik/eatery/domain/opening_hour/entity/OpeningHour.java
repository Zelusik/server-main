package com.zelusik.eatery.domain.opening_hour.entity;

import com.zelusik.eatery.domain.place.constant.DayOfWeek;
import com.zelusik.eatery.domain.place.entity.Place;
import com.zelusik.eatery.global.common.entity.BaseTimeEntity;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.time.LocalTime;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@Table(indexes = {
        @Index(name = "idx__opening_hour__day_of_week", columnList = "dayOfWeek")
})
@Entity
public class OpeningHour extends BaseTimeEntity {

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

    public static OpeningHour of(Place place, DayOfWeek dayOfWeek, LocalTime openAt, LocalTime closeAt) {
        return new OpeningHour(null, place, dayOfWeek, openAt, closeAt, null, null);
    }

    public static OpeningHour of(Long id, Place place, DayOfWeek dayOfWeek, LocalTime openAt, LocalTime closeAt, LocalDateTime createdAt, LocalDateTime updatedAt) {
        return new OpeningHour(id, place, dayOfWeek, openAt, closeAt, createdAt, updatedAt);
    }

    private OpeningHour(Long id, Place place, DayOfWeek dayOfWeek, LocalTime openAt, LocalTime closeAt, LocalDateTime createdAt, LocalDateTime updatedAt) {
        super(createdAt, updatedAt);
        this.id = id;
        this.place = place;
        this.dayOfWeek = dayOfWeek;
        this.openAt = openAt;
        this.closeAt = closeAt;
    }
}
