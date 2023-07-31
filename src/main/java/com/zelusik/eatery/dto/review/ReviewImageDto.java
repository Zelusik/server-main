package com.zelusik.eatery.dto.review;

import com.zelusik.eatery.domain.review.ReviewImage;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.lang.Nullable;

import java.time.LocalDateTime;
import java.util.List;

@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Getter
public class ReviewImageDto {

    private Long id;

    private Long reviewId;

    private String originalName;

    private String storedName;

    private String url;

    private String thumbnailStoredName;

    private String thumbnailUrl;

    @Nullable
    private List<ReviewImageMenuTagDto> menuTags;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

    private LocalDateTime deletedAt;
    
    public static ReviewImageDto of(Long id, Long reviewId, String originalName, String storedName, String url, String thumbnailStoredName, String thumbnailUrl, List<ReviewImageMenuTagDto> menuTags, LocalDateTime createdAt, LocalDateTime updatedAt, LocalDateTime deletedAt) {
        return new ReviewImageDto(id, reviewId, originalName, storedName, url, thumbnailStoredName, thumbnailUrl, menuTags, createdAt, updatedAt, deletedAt);
    }

    public static ReviewImageDto of(Long id, Long reviewId, String originalName, String storedName, String url, String thumbnailStoredName, String thumbnailUrl, LocalDateTime createdAt, LocalDateTime updatedAt, LocalDateTime deletedAt) {
        return of(id, reviewId, originalName, storedName, url, thumbnailStoredName, thumbnailUrl, null, createdAt, updatedAt, deletedAt);
    }

    public static ReviewImageDto from(ReviewImage entity) {
        return of(
                entity.getId(),
                entity.getReview().getId(),
                entity.getOriginalName(),
                entity.getStoredName(),
                entity.getUrl(),
                entity.getThumbnailStoredName(),
                entity.getThumbnailUrl(),
                entity.getMenuTags().stream()
                        .map(ReviewImageMenuTagDto::from)
                        .toList(),
                entity.getCreatedAt(),
                entity.getUpdatedAt(),
                entity.getDeletedAt()
        );
    }
}
