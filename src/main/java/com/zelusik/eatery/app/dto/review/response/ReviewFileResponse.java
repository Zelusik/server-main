package com.zelusik.eatery.app.dto.review.response;

import com.zelusik.eatery.app.dto.review.ReviewFileDto;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Getter
public class ReviewFileResponse {

    private Long id;
    private String name;
    private String url;

    public static ReviewFileResponse of(Long id, String name, String url) {
        return new ReviewFileResponse(id, name, url);
    }

    public static ReviewFileResponse from(ReviewFileDto dto) {
        return of(dto.id(), dto.originalName(), dto.url());
    }
}
