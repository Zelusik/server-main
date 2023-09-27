package com.zelusik.eatery.domain.place.entity;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.lang.NonNull;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import java.util.Objects;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Getter
@Embeddable
public class Point {

    @Schema(description = "위도", example = "37.5595073462493")
    @NonNull
    @Column(nullable = false)
    private String lat;

    @Schema(description = "경도", example = "126.921462488105")
    @NonNull
    @Column(nullable = false)
    private String lng;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Point that)) return false;
        return Objects.equals(this.getLat(), that.getLat())
                && Objects.equals(this.getLng(), that.getLng());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getLat(), getLng());
    }
}
