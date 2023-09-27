package com.zelusik.eatery.unit.service;

import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.amazonaws.services.s3.model.PutObjectResult;
import com.zelusik.eatery.dto.file.S3FileDto;
import com.zelusik.eatery.service.FileService;
import com.zelusik.eatery.util.MultipartFileTestUtils;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;

@DisplayName("[Unit] S3 File Service")
@ExtendWith(MockitoExtension.class)
class FileServiceTest {

    @InjectMocks
    private FileService sut;

    @Mock
    AmazonS3Client s3Client;

    @DisplayName("Multipart file이 주어지면, file을 업로드한다.")
    @Test
    void givenMultipartFile_whenUpload_thenUploadFile() {
        // given
        String expectedStoredFileUrl = "url";
        given(s3Client.putObject(any(PutObjectRequest.class))).willReturn(new PutObjectResult());
        given(s3Client.getResourceUrl(any(), any(String.class))).willReturn(expectedStoredFileUrl);

        // when
        S3FileDto actualFileDto = sut.uploadFile(createMockMultipartFile(), "test");

        // then
        then(s3Client).should().putObject(any(PutObjectRequest.class));
        then(s3Client).should().getResourceUrl(any(), any(String.class));
        assertThat(actualFileDto.getUrl()).isEqualTo(expectedStoredFileUrl);
    }

    private MockMultipartFile createMockMultipartFile() {
        return new MockMultipartFile(
                "test",
                "test.txt",
                MediaType.TEXT_PLAIN_VALUE,
                "test".getBytes()
        );
    }
}