package com.zelusik.eatery.dto.review;

import com.zelusik.eatery.domain.review.MenuTagPoint;
import com.zelusik.eatery.domain.review.ReviewImage;
import com.zelusik.eatery.domain.review.ReviewImageMenuTag;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.lang.NonNull;

@AllArgsConstructor(access = AccessLevel.PRIVATE)
@NoArgsConstructor(access = AccessLevel.PRIVATE)
@Getter
public class ReviewImageMenuTagDto {

    private Long id;
    private Long reviewImageId;
    private String content;
    private MenuTagPoint point;

    public static ReviewImageMenuTagDto of(@NonNull String content, @NonNull MenuTagPoint point) {
        return new ReviewImageMenuTagDto(null, null, content, point);
    }

    public static ReviewImageMenuTagDto of(Long id, Long reviewImageId, String content, MenuTagPoint point) {
        return new ReviewImageMenuTagDto(id, reviewImageId, content, point);
    }

    public static ReviewImageMenuTagDto from(ReviewImageMenuTag entity) {
        return of(entity.getId(), entity.getReviewImage().getId(), entity.getContent(), entity.getPoint());
    }

    public ReviewImageMenuTag toEntity(@NonNull ReviewImage reviewImage) {
        return ReviewImageMenuTag.of(reviewImage, getContent(), getPoint());
    }
}
