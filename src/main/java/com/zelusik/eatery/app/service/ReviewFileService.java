package com.zelusik.eatery.app.service;

import com.zelusik.eatery.app.domain.Review;
import com.zelusik.eatery.app.domain.ReviewFile;
import com.zelusik.eatery.app.dto.file.S3FileDto;
import com.zelusik.eatery.app.repository.ReviewFileRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RequiredArgsConstructor
@Transactional(readOnly = true)
@Service
public class ReviewFileService {

    private final FileService fileService;
    private final ReviewFileRepository reviewFileRepository;

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
            S3FileDto s3FileDto = fileService.upload(multipartFile, DIR_PATH);
            review.getReviewFiles().add(ReviewFile.of(
                    review,
                    s3FileDto.originalName(),
                    s3FileDto.storedName(),
                    s3FileDto.url()
            ));
        });
        reviewFileRepository.saveAll(review.getReviewFiles());
    }

    /**
     * ReviewFile들을 삭제한다.
     *
     * @param reviewFiles 삭제할 ReviewFile 목록
     */
    @Transactional
    public void deleteAll(List<ReviewFile> reviewFiles) {
        reviewFileRepository.deleteAll(reviewFiles);
    }
}
