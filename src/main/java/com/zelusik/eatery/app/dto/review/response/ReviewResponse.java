package com.zelusik.eatery.app.dto.review.response;

import com.zelusik.eatery.app.domain.constant.ReviewKeyword;
import com.zelusik.eatery.app.dto.place.response.PlaceResponse;
import com.zelusik.eatery.app.dto.review.ReviewDto;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Getter
public class ReviewResponse {

    private Long id;
    private Long uploaderId;
    private PlaceResponse place;
    private List<ReviewKeyword> keywords;
    private String content;
    private List<ReviewFileResponse> reviewFiles;

    public static ReviewResponse of(Long id, Long uploaderId, PlaceResponse place, List<ReviewKeyword> keywords, String content, List<ReviewFileResponse> reviewFiles) {
        return new ReviewResponse(id, uploaderId, place, keywords, content, reviewFiles);
    }

    public static ReviewResponse from(ReviewDto reviewDto) {
        return new ReviewResponse(
                reviewDto.id(),
                reviewDto.uploaderDto().id(),
                PlaceResponse.from(reviewDto.placeDto()),
                reviewDto.keywords(),
                reviewDto.content(),
                reviewDto.reviewFileDtos().stream()
                        .map(ReviewFileResponse::from)
                        .toList()
        );
    }
}
