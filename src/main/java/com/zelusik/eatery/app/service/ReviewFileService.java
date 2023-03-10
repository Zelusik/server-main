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
}
