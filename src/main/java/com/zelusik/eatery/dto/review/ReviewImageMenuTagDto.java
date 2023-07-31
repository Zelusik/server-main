package com.zelusik.eatery.dto.review;

import com.zelusik.eatery.domain.review.MenuTagPoint;
import com.zelusik.eatery.domain.review.ReviewImageMenuTag;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@AllArgsConstructor(access = AccessLevel.PRIVATE)
@NoArgsConstructor(access = AccessLevel.PRIVATE)
@Getter
public class ReviewImageMenuTagDto {

    private Long id;
    private Long reviewImageId;
    private String content;
    private MenuTagPoint point;

    public static ReviewImageMenuTagDto of(Long id, Long reviewImageId, String content, MenuTagPoint point) {
        return new ReviewImageMenuTagDto(id, reviewImageId, content, point);
    }

    // TODO: 이 시점에서 ReviewImageMenuTag.reviewImage의 lazy loading이 될 수 있으므로 고려한 코드 작성 필요
    public static ReviewImageMenuTagDto from(ReviewImageMenuTag entity) {
        return of(entity.getId(), entity.getReviewImage().getId(), entity.getContent(), entity.getPoint());
    }

    // ReviewImage를 DB에서 실제 조회(lazy loading)하지 않고 ReviewImageMenuTag를 생성하기 위한 reviewImageId를 따로 전달받는다.
    public static ReviewImageMenuTagDto from(ReviewImageMenuTag entity, Long reviewImageId) {
        return of(entity.getId(), reviewImageId, entity.getContent(), entity.getPoint());
    }
}
