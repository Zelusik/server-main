package com.zelusik.eatery.app.dto.review;

import com.zelusik.eatery.app.domain.review.ReviewFile;

import java.time.LocalDateTime;

public record ReviewFileDto(
        Long id,
        Long reviewId,
        String originalName,
        String storedName,
        String url,
        String thumbnailStoredName,
        String thumbnailUrl,
        LocalDateTime createdAt,
        LocalDateTime updatedAt,
        LocalDateTime deletedAt
) {

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
