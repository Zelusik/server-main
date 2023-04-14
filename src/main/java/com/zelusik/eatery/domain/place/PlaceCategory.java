package com.zelusik.eatery.domain.place;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import java.util.Arrays;

@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@Embeddable
public class PlaceCategory {

    @Schema(description = "카테고리 1", example = "퓨전요리")
    @Column(nullable = false)
    private String firstCategory;

    @Schema(description = "카테고리 2", example = "퓨전일식")
    private String secondCategory;

    @Schema(description = "카테고리 3")
    private String thirdCategory;

    public PlaceCategory(String categoryName) {
        String[] categories = categoryName.split(" > ");
        int length = categories.length;

        this.firstCategory = length > 1 ? categories[1] : null;
        this.secondCategory = length > 2 ? categories[2] : null;
        this.thirdCategory = length > 3 ? categories[3] : null;
    }
}
