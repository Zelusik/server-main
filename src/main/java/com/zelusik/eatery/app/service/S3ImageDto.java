package com.zelusik.eatery.app.service;

public record S3ImageDto(
        String originalName,
        String storedName,
        String url,
        String thumbnailStoredName,
        String thumbnailUrl
) {

    public static S3ImageDto of(String originalName, String storedName, String url, String thumbnailStoredName, String thumbnailUrl) {
        return new S3ImageDto(originalName, storedName, url, thumbnailStoredName, thumbnailUrl);
    }
}
