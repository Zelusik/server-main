package com.zelusik.eatery.util;

import com.zelusik.eatery.dto.file.S3FileDto;
import com.zelusik.eatery.service.S3ImageDto;

public class S3FileTestUtils {
    
    public static S3FileDto createS3FileDto() {
        return S3FileDto.of(
                "originalFileName",
                "storedFileName",
                "url"
        );
    }

    public static S3ImageDto createS3ImageDto() {
        return S3ImageDto.of(
                "originalFileName",
                "storedFileName",
                "url",
                "thumbnailStoredFileName",
                "thumbnailUrl"
        );
    }
}
