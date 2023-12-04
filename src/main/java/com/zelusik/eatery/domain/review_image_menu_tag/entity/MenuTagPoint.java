package com.zelusik.eatery.domain.review_image_menu_tag.entity;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import javax.validation.constraints.NotNull;
import java.util.Objects;

@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@Embeddable
public class MenuTagPoint {

    @Schema(description = "메뉴 태그의 x좌표", example = "30.45")
    @NotNull
    @Column(nullable = false)
    private Double x;

    @Schema(description = "메뉴 태그의 y좌표", example = "12.7504")
    @NotNull
    @Column(nullable = false)
    private Double y;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof MenuTagPoint that)) return false;
        return Objects.equals(this.getX(), that.getX())
                && Objects.equals(this.getY(), that.getY());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getX(), getY());
    }
}
