package com.zelusik.eatery.domain.review_image.service;

import com.zelusik.eatery.domain.review.entity.Review;
import com.zelusik.eatery.domain.review_image.dto.request.ReviewImageCreateRequest;
import com.zelusik.eatery.domain.review_image.entity.ReviewImage;
import com.zelusik.eatery.domain.review_image.repository.ReviewImageRepository;
import com.zelusik.eatery.global.common.exception.AsyncExecutionException;
import com.zelusik.eatery.global.common.exception.AsyncInterruptedException;
import com.zelusik.eatery.global.file.dto.S3ImageDto;
import com.zelusik.eatery.global.file.service.S3FileService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.concurrent.ListenableFuture;

import javax.validation.constraints.NotNull;
import java.util.List;
import java.util.concurrent.ExecutionException;

@RequiredArgsConstructor
@Transactional
@Service
public class ReviewImageCommandService {

    private final S3FileService s3FileService;
    private final ReviewImageRepository reviewImageRepository;

    public static final String AWS_S3_DIR_PATH = "review/";

    /**
     * Review 첨부 파일을 S3 bucket에 업로드하고, ReviewImage entity를 생성하여 DB에 저장한다.
     *
     * @param review                    이미지가 첨부될 review
     * @param reviewImageCreateRequests 업로드할 image들에 대한 정보가 담긴 dto
     * @return 업로드 및 저장된 review images
     */
    public List<ReviewImage> uploadReviewImages(
            @NotNull Review review,
            @NotNull List<ReviewImageCreateRequest> reviewImageCreateRequests
    ) {
        List<ListenableFuture<S3ImageDto>> futures = reviewImageCreateRequests.stream()
                .map(imageReq -> s3FileService.asyncUploadImageWithResizing(imageReq.getImage(), AWS_S3_DIR_PATH))
                .toList();

        List<ReviewImage> reviewImages = futures.stream()
                .map(future -> {
                    try {
                        S3ImageDto uploadedImageDto = future.get();
                        return createNewReviewImage(review, uploadedImageDto);
                    } catch (ExecutionException ex) {
                        throw new AsyncExecutionException(ex);
                    } catch (InterruptedException ex) {
                        throw new AsyncInterruptedException(ex);
                    }
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

    private static ReviewImage createNewReviewImage(Review review, S3ImageDto uploadedImageDto) {
        return ReviewImage.createNewReviewImage(
                review,
                uploadedImageDto.getOriginalName(),
                uploadedImageDto.getStoredName(),
                uploadedImageDto.getUrl(),
                uploadedImageDto.getThumbnailStoredName(),
                uploadedImageDto.getThumbnailUrl()
        );
    }
}
