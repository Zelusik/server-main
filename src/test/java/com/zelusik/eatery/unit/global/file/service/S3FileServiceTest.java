package com.zelusik.eatery.unit.global.file.service;

import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.amazonaws.services.s3.model.PutObjectResult;
import com.zelusik.eatery.global.common.properties.AWSProperties;
import com.zelusik.eatery.global.file.dto.S3FileDto;
import com.zelusik.eatery.global.file.service.S3FileService;
import org.junit.jupiter.api.BeforeEach;
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

@DisplayName("[Unit] Service - S3")
@ExtendWith(MockitoExtension.class)
class S3FileServiceTest {

    @InjectMocks
    private S3FileService sut;

    @Mock
    private AmazonS3Client s3Client;

    @Mock
    private AWSProperties awsProperties;
    @Mock
    private AWSProperties.S3 s3;
    @Mock
    private AWSProperties.CloudFront cloudFront;

    @BeforeEach
    void setUp() {
        given(awsProperties.s3()).willReturn(s3);
        given(awsProperties.cloudFront()).willReturn(cloudFront);
        given(s3.bucketName()).willReturn("aws-s3-bucket-name");
        given(cloudFront.domainName()).willReturn("aws-cloud-front-domain-name/");
    }

    @DisplayName("Multipart file이 주어지면, file을 업로드한다.")
    @Test
    void givenMultipartFile_whenUpload_thenUploadFile() {
        // given
        given(s3Client.putObject(any(PutObjectRequest.class))).willReturn(new PutObjectResult());

        // when
        S3FileDto actualFileDto = sut.uploadFile(createMockMultipartFile(), "test");

        // then
        then(s3Client).should().putObject(any(PutObjectRequest.class));
        assertThat(actualFileDto.getUrl()).contains(awsProperties.cloudFront().domainName());
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