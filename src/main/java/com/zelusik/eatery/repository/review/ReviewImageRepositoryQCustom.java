package com.zelusik.eatery.repository.review;

import com.zelusik.eatery.domain.place.Place;
import com.zelusik.eatery.domain.review.ReviewImage;

import java.util.List;

public interface ReviewImageRepositoryQCustom {

    List<ReviewImage> findLatest3ByPlace(Long placeId);
}
