package com.zelusik.eatery.util;

import com.zelusik.eatery.dto.ImageDto;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;

public class MultipartFileTestUtils {

    public static ImageDto createMockImageDto() {
        return ImageDto.of(
                createMockMultipartFile(),
                createMockMultipartFile()
        );
    }

    public static MockMultipartFile createMockMultipartFile() {
        return new MockMultipartFile(
                "test",
                "test.txt",
                MediaType.TEXT_PLAIN_VALUE,
                "test".getBytes()
        );
    }
}
