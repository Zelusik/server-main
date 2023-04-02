package com.zelusik.eatery.app.service;

import com.zelusik.eatery.app.domain.review.Review;
import com.zelusik.eatery.app.dto.ImageDto;
import com.zelusik.eatery.app.repository.review.ReviewImageRepository;
import com.zelusik.eatery.util.MultipartFileTestUtils;
import com.zelusik.eatery.util.ReviewTestUtils;
import com.zelusik.eatery.util.S3FileTestUtils;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@DisplayName("[Service] Review File")
@ExtendWith(MockitoExtension.class)
class ReviewImageServiceTest {

    @InjectMocks
    private ReviewImageService sut;

    @Mock
    private FileService fileService;
    @Mock
    private ReviewImageRepository reviewImageRepository;

    @DisplayName("이미지 파일들이 주어지면, 파일들을 업로드한다.")
    @Test
    void givenImageFiles_whenUploading_thenUploadFiles() {
        // given
        List<ImageDto> images = List.of(MultipartFileTestUtils.createMockImageDto());
        Review review = ReviewTestUtils.createReviewWithId(1L, "1");
        given(fileService.uploadFile(any(MultipartFile.class), any(String.class)))
                .willReturn(S3FileTestUtils.createS3FileDto());
        given(reviewImageRepository.saveAll(any())).willReturn(List.of());

        // when
        sut.upload(review, images);

        // then
        verify(fileService, times(2)).uploadFile(any(MultipartFile.class), any(String.class));
        then(reviewImageRepository).should().saveAll(any());
    }
}