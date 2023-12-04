package com.zelusik.eatery.domain.review_image.repository;

import com.zelusik.eatery.domain.review_image.entity.ReviewImage;

import java.util.List;

public interface ReviewImageRepositoryQCustom {

    List<ReviewImage> findLatest3ByPlace(Long placeId);
}
