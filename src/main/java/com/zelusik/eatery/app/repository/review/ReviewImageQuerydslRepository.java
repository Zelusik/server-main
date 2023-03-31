package com.zelusik.eatery.app.repository.review;

import com.zelusik.eatery.app.domain.place.Place;
import com.zelusik.eatery.app.domain.review.ReviewImage;

import java.util.List;

public interface ReviewImageQuerydslRepository {

    List<ReviewImage> findLatest3ByPlace(Place place);
}
