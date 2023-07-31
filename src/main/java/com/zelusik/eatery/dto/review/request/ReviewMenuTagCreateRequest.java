package com.zelusik.eatery.dto.review.request;

import com.zelusik.eatery.domain.review.MenuTagPoint;
import com.zelusik.eatery.dto.review.ReviewImageMenuTagDto;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Setter // for @ModelAttribute
@Getter
public class ReviewMenuTagCreateRequest {

    @Schema(description = "메뉴 태그 내용", example = "페퍼로니 피자")
    @NotBlank
    private String content;

    @NotNull
    private MenuTagPoint point;

    public ReviewImageMenuTagDto toDto() {
        return ReviewImageMenuTagDto.of(getContent(), getPoint());
    }
}
