package com.zelusik.eatery.app.dto.file;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Getter
public class S3FileDto {

    private String originalName;
    private String storedName;
    private String url;

    public static S3FileDto of(String originalName, String storedName, String url) {
        return new S3FileDto(originalName, storedName, url);
    }
}
