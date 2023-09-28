package com.zelusik.eatery.domain.review_image.service;

import com.zelusik.eatery.domain.review_image.dto.ReviewImageDto;
import com.zelusik.eatery.domain.review_image.repository.ReviewImageRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@RequiredArgsConstructor
@Transactional(readOnly = true)
@Service
public class ReviewImageQueryService {

    private final ReviewImageRepository reviewImageRepository;

    public List<ReviewImageDto> findLatest3ByPlace(Long placeId) {
        return reviewImageRepository.findLatest3ByPlace(placeId).stream()
                .map(ReviewImageDto::from)
                .toList();
    }
}
