package com.zelusik.eatery.domain.review_image.service;

import com.zelusik.eatery.domain.review.entity.Review;
import com.zelusik.eatery.domain.review_image.dto.request.ReviewImageCreateRequest;
import com.zelusik.eatery.domain.review_image.entity.ReviewImage;
import com.zelusik.eatery.domain.review_image.repository.ReviewImageRepository;
import com.zelusik.eatery.global.file.dto.S3ImageDto;
import com.zelusik.eatery.global.file.service.S3FileService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@RequiredArgsConstructor
@Transactional
@Service
public class ReviewImageCommandService {

    private final S3FileService s3FileService;
    private final ReviewImageRepository reviewImageRepository;

    private static final String DIR_PATH = "review/";

    /**
     * Review 첨부 파일을 S3 bucket에 업로드하고,
     * ReviewImage entity를 생성하여 DB에 저장한다.
     *
     * @param review 파일이 첨부될 리뷰
     * @param images 업로드할 이미지들
     */
    public List<ReviewImage> upload(Review review, List<ReviewImageCreateRequest> images) {
        List<ReviewImage> reviewImages = images.stream().map(image -> {
            S3ImageDto imageDto = s3FileService.uploadImageWithResizing(image.getImage(), DIR_PATH);
            return ReviewImage.createNewReviewImage(
                    review,
                    imageDto.getOriginalName(),
                    imageDto.getStoredName(),
                    imageDto.getUrl(),
                    imageDto.getThumbnailStoredName(),
                    imageDto.getThumbnailUrl()
            );
        }).toList();
        return reviewImageRepository.saveAll(reviewImages);
    }

    /**
     * ReviewFile들을 삭제한다.
     *
     * @param reviewImages 삭제할 ReviewImage 목록
     */
    public void softDeleteAll(List<ReviewImage> reviewImages) {
        reviewImages.forEach(ReviewImage::softDelete);
        reviewImageRepository.flush();
    }
}
