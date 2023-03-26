package com.zelusik.eatery.app.dto.file.response;

import com.zelusik.eatery.app.dto.review.ReviewFileDto;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Getter
public class ImageResponse {

    private String url;
    private String thumbnailUrl;

    public static ImageResponse of(String url, String thumbnailUrl) {
        return new ImageResponse(url, thumbnailUrl);
    }

    public static ImageResponse from(ReviewFileDto reviewFileDto) {
        return of(reviewFileDto.url(), reviewFileDto.thumbnailUrl());
    }
}
