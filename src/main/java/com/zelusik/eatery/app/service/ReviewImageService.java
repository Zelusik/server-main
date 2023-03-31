package com.zelusik.eatery.app.service;

import com.zelusik.eatery.app.domain.place.Place;
import com.zelusik.eatery.app.domain.review.Review;
import com.zelusik.eatery.app.domain.review.ReviewImage;
import com.zelusik.eatery.app.dto.review.ReviewImageDto;
import com.zelusik.eatery.app.repository.review.ReviewImageRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RequiredArgsConstructor
@Transactional(readOnly = true)
@Service
public class ReviewImageService {

    private final FileService fileService;
    private final ReviewImageRepository reviewImageRepository;

    private static final String DIR_PATH = "review/";

    /**
     * Review 첨부 파일을 S3 bucket에 업로드한다.
     *
     * @param review         파일이 첨부될 리뷰
     * @param multipartFiles 업로드할 파일들
     */
    @Transactional
    public void upload(Review review, List<MultipartFile> multipartFiles) {
        multipartFiles.forEach(multipartFile -> {
            S3ImageDto s3ImageDto = fileService.uploadImage(multipartFile, DIR_PATH);
            review.getReviewImages().add(ReviewImage.of(
                    review,
                    s3ImageDto.originalName(),
                    s3ImageDto.storedName(),
                    s3ImageDto.url(),
                    s3ImageDto.thumbnailStoredName(),
                    s3ImageDto.thumbnailUrl()
            ));
        });
        reviewImageRepository.saveAll(review.getReviewImages());
    }

    public List<ReviewImageDto> findLatest3ByPlace(Place place) {
        return reviewImageRepository.findLatest3ByPlace(place).stream()
                .map(ReviewImageDto::from)
                .toList();
    }

    /**
     * ReviewFile들을 삭제한다.
     *
     * @param reviewImages 삭제할 ReviewImage 목록
     */
    @Transactional
    public void deleteAll(List<ReviewImage> reviewImages) {
        reviewImageRepository.deleteAll(reviewImages);
    }
}
