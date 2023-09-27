package com.zelusik.eatery.global.file.dto;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Getter
public class S3ImageDto {

    private String originalName;
    private String storedName;
    private String url;
    private String thumbnailStoredName;
    private String thumbnailUrl;

    public static S3ImageDto of(String originalName, String storedName, String url, String thumbnailStoredName, String thumbnailUrl) {
        return new S3ImageDto(originalName, storedName, url, thumbnailStoredName, thumbnailUrl);
    }
}
