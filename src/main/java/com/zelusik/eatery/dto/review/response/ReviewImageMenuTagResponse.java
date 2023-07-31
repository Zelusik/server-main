package com.zelusik.eatery.dto.review.response;

import com.zelusik.eatery.domain.review.MenuTagPoint;
import com.zelusik.eatery.dto.review.ReviewImageMenuTagDto;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PRIVATE)
@Getter
public class ReviewImageMenuTagResponse {

    @Schema(description = "메뉴 이름", example = "떡볶이")
    private String content;

    private MenuTagPoint point;

    public static ReviewImageMenuTagResponse from(ReviewImageMenuTagDto dto) {
        return new ReviewImageMenuTagResponse(dto.getContent(), dto.getPoint());
    }
}
