package com.zelusik.eatery.app.domain.place;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import java.util.Arrays;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@Embeddable
public class PlaceCategory {

    @Column(nullable = false)
    private String firstCategory;

    private String secondCategory;

    private String thirdCategory;

    public PlaceCategory(String categoryName) {
        String[] categories = categoryName.split(" > ");
        int length = categories.length;

        this.firstCategory = length > 1 ? categories[1] : null;
        this.secondCategory = length > 2 ? categories[2] : null;
        this.thirdCategory = length > 3 ? categories[3] : null;
    }
}
