package com.zelusik.eatery.util;

import com.zelusik.eatery.app.dto.file.S3FileDto;

public class S3FileTestUtils {
    
    public static S3FileDto createS3FileDto() {
        return S3FileDto.of(
                "originalFileName",
                "storedFileName",
                "url"
        );
    }
}
