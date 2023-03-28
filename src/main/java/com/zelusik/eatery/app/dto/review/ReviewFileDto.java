package com.zelusik.eatery.app.dto.review;

import com.zelusik.eatery.app.domain.review.ReviewFile;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDateTime;

@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Getter
public class ReviewFileDto {

    private Long id;
    private Long reviewId;
    private String originalName;
    private String storedName;
    private String url;
    private String thumbnailStoredName;
    private String thumbnailUrl;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private LocalDateTime deletedAt;

    public static ReviewFileDto of(Long id, Long reviewId, String originalName, String storedName, String url, String thumbnailStoredName, String thumbnailUrl, LocalDateTime createdAt, LocalDateTime updatedAt, LocalDateTime deletedAt) {
        return new ReviewFileDto(id, reviewId, originalName, storedName, url, thumbnailStoredName, thumbnailUrl, createdAt, updatedAt, deletedAt);
    }

    public static ReviewFileDto from(ReviewFile entity) {
        return of(
                entity.getId(),
                entity.getReview().getId(),
                entity.getOriginalName(),
                entity.getStoredName(),
                entity.getUrl(),
                entity.getThumbnailStoredName(),
                entity.getThumbnailUrl(),
                entity.getCreatedAt(),
                entity.getUpdatedAt(),
                entity.getDeletedAt()
        );
    }
}
