package com.zelusik.eatery.unit.service;

import com.zelusik.eatery.domain.review.Review;
import com.zelusik.eatery.dto.review.request.ReviewImageCreateRequest;
import com.zelusik.eatery.repository.review.ReviewImageRepository;
import com.zelusik.eatery.service.FileService;
import com.zelusik.eatery.service.ReviewImageService;
import com.zelusik.eatery.service.S3ImageDto;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

import static com.zelusik.eatery.util.ReviewTestUtils.createReview;
import static com.zelusik.eatery.util.ReviewTestUtils.createReviewImageCreateRequest;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;

@DisplayName("[Unit] Review File Service")
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
        List<ReviewImageCreateRequest> images = List.of(createReviewImageCreateRequest());
        Review review = createReview(1L, 2L, 3L, "3", 4L, 5L);
        given(fileService.uploadImageWithResizing(any(MultipartFile.class), any(String.class))).willReturn(createS3ImageDto());
        given(reviewImageRepository.saveAll(any())).willReturn(List.of());

        // when
        sut.upload(review, images);

        // then
        then(fileService).should().uploadImageWithResizing(any(MultipartFile.class), any(String.class));
        then(reviewImageRepository).should().saveAll(any());
        then(fileService).shouldHaveNoMoreInteractions();
        then(reviewImageRepository).shouldHaveNoMoreInteractions();
    }

    private S3ImageDto createS3ImageDto() {
        return S3ImageDto.of(
                "originalFileName",
                "storedFileName",
                "url",
                "thumbnailStoredFileName",
                "thumbnailUrl"
        );
    }
}