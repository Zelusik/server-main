package com.zelusik.eatery.app.service;

import com.zelusik.eatery.app.domain.place.Place;
import com.zelusik.eatery.app.domain.review.Review;
import com.zelusik.eatery.app.domain.review.ReviewImage;
import com.zelusik.eatery.app.dto.ImageDto;
import com.zelusik.eatery.app.dto.file.S3FileDto;
import com.zelusik.eatery.app.dto.review.ReviewImageDto;
import com.zelusik.eatery.app.repository.review.ReviewImageRepository;
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
     * Review 첨부 파일을 S3 bucket에 업로드한다.
     *
     * @param review 파일이 첨부될 리뷰
     * @param images 업로드할 이미지들
     */
    @Transactional
    public void upload(Review review, List<ImageDto> images) {
        images.forEach(imageDto -> {
            S3FileDto image = fileService.uploadFile(imageDto.getImage(), DIR_PATH);
            S3FileDto thumbnailImage = fileService.uploadFile(imageDto.getThumbnailImage(), DIR_PATH + "thumbnail/");
            review.getReviewImages().add(ReviewImage.of(
                    review,
                    image.getOriginalName(),
                    image.getStoredName(),
                    image.getUrl(),
                    thumbnailImage.getStoredName(),
                    thumbnailImage.getUrl()
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
    public void softDeleteAll(List<ReviewImage> reviewImages) {
        reviewImages.forEach(ReviewImage::softDelete);
        reviewImageRepository.flush();
    }
}
