package com.zelusik.eatery.app.dto.file;

public record S3FileDto(
        String originalName,
        String storedName,
        String url
) {

    public static S3FileDto of(String originalName, String storedName, String url) {
        return new S3FileDto(originalName, storedName, url);
    }
}
