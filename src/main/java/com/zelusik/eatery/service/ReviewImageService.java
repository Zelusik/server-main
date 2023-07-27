package com.zelusik.eatery.service;

import com.zelusik.eatery.domain.review.Review;
import com.zelusik.eatery.domain.review.ReviewImage;
import com.zelusik.eatery.dto.review.ReviewImageDto;
import com.zelusik.eatery.dto.review.request.ReviewImageCreateRequest;
import com.zelusik.eatery.repository.review.ReviewImageRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@RequiredArgsConstructor
@Transactional(readOnly = true)
@Service
public class ReviewImageService {

    private final FileService fileService;
    private final ReviewImageRepository reviewImageRepository;

    private static final String DIR_PATH = "review/";

    /**
     * Review 첨부 파일을 S3 bucket에 업로드하고,
     * ReviewImage entity를 생성하여 DB에 저장한다.
     *
     * @param review 파일이 첨부될 리뷰
     * @param images 업로드할 이미지들
     */
    @Transactional
    public void upload(Review review, List<ReviewImageCreateRequest> images) {
        images.forEach(image -> {
            S3ImageDto imageDto = fileService.uploadImageWithResizing(image.getImage(), DIR_PATH);
            ReviewImage reviewImage = ReviewImage.of(
                    review,
                    imageDto.getOriginalName(),
                    imageDto.getStoredName(),
                    imageDto.getUrl(),
                    imageDto.getThumbnailStoredName(),
                    imageDto.getThumbnailUrl()
            );
            review.getReviewImages().add(reviewImage);
        });
        reviewImageRepository.saveAll(review.getReviewImages());
    }

    public List<ReviewImageDto> findLatest3ByPlace(Long placeId) {
        return reviewImageRepository.findLatest3ByPlace(placeId).stream()
                .map(ReviewImageDto::from)
                .toList();
    }

    /**
     * ReviewFile들을 삭제한다.
     *
     * @param reviewImages 삭제할 ReviewImage 목록
     */
    @Transactional
    public void softDeleteAll(List<ReviewImage> reviewImages) {
        reviewImages.forEach(ReviewImage::softDelete);
        reviewImageRepository.flush();
    }
}
