package com.zelusik.eatery.dto.review;

import com.zelusik.eatery.domain.review.ReviewImage;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.lang.Nullable;

import java.time.LocalDateTime;
import java.util.List;

@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PRIVATE)
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

    public ReviewImageDto(Long id, Long reviewId, String originalName, String storedName, String url, String thumbnailStoredName, String thumbnailUrl) {
        this(id, reviewId, originalName, storedName, url, thumbnailStoredName, thumbnailUrl, null);
    }

    public static ReviewImageDto from(ReviewImage entity) {
        return new ReviewImageDto(
                entity.getId(),
                entity.getReview().getId(),
                entity.getOriginalName(),
                entity.getStoredName(),
                entity.getUrl(),
                entity.getThumbnailStoredName(),
                entity.getThumbnailUrl(),
                entity.getMenuTags().stream()
                        .map(ReviewImageMenuTagDto::from)
                        .toList()
        );
    }
}
