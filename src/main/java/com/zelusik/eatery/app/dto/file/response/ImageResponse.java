package com.zelusik.eatery.app.dto.file.response;

import com.zelusik.eatery.app.dto.review.ReviewFileDto;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Getter
public class ImageResponse {

    @Schema(description = "이미지 url", example = "https://...")
    private String url;

    @Schema(description = "썸네일 이미지 url", example = "https://...")
    private String thumbnailUrl;

    public static ImageResponse of(String url, String thumbnailUrl) {
        return new ImageResponse(url, thumbnailUrl);
    }

    public static ImageResponse from(ReviewFileDto reviewFileDto) {
        return of(reviewFileDto.url(), reviewFileDto.thumbnailUrl());
    }
}
