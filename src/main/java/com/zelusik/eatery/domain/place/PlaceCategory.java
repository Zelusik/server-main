package com.zelusik.eatery.domain.place;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import java.util.Objects;

@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@Embeddable
public class PlaceCategory {

    @Schema(description = "카테고리 1", example = "퓨전요리")
    @NonNull
    @Column(nullable = false)
    private String firstCategory;

    @Schema(description = "카테고리 2", example = "퓨전일식")
    @Nullable
    private String secondCategory;

    @Schema(description = "카테고리 3")
    @Nullable
    private String thirdCategory;

    public static PlaceCategory of(String categoryName) {
        String[] categories = categoryName.split(" > ");
        int length = categories.length;

        String firstCategory = length > 1 ? categories[1] : "";
        String secondCategory = length > 2 ? categories[2] : null;
        String thirdCategory = length > 3 ? categories[3] : null;

        return new PlaceCategory(firstCategory, secondCategory, thirdCategory);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof PlaceCategory that)) return false;
        return Objects.equals(this.getFirstCategory(), that.getFirstCategory())
                && Objects.equals(this.getSecondCategory(), that.getSecondCategory())
                && Objects.equals(this.getThirdCategory(), that.getThirdCategory());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getFirstCategory(), getSecondCategory(), getThirdCategory());
    }
}
