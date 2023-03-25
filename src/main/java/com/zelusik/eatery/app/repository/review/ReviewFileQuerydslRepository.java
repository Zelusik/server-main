package com.zelusik.eatery.app.repository.review;

import com.zelusik.eatery.app.domain.place.Place;
import com.zelusik.eatery.app.domain.review.ReviewFile;

import java.util.List;

public interface ReviewFileQuerydslRepository {

    List<ReviewFile> findLatest3ByPlace(Place place);
}
