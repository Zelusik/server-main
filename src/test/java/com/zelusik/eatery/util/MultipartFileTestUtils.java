package com.zelusik.eatery.util;

import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;

public class MultipartFileTestUtils {

    public static MockMultipartFile createMockMultipartFile() {
        return new MockMultipartFile(
                "test",
                "test.txt",
                MediaType.TEXT_PLAIN_VALUE,
                "test".getBytes()
        );
    }
}
