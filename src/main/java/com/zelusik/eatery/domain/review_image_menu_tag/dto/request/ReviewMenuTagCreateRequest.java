package com.zelusik.eatery.domain.review_image_menu_tag.dto.request;

import com.zelusik.eatery.domain.review_image_menu_tag.dto.request.MenuTagPointCreateRequest;
import com.zelusik.eatery.domain.review_image_menu_tag.entity.MenuTagPoint;
import com.zelusik.eatery.domain.review_image_menu_tag.dto.ReviewImageMenuTagDto;
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
    private MenuTagPointCreateRequest point;

    public ReviewImageMenuTagDto toDto() {
        return ReviewImageMenuTagDto.of(
                getContent(),
                new MenuTagPoint(
                        Double.parseDouble(point.getX()),
                        Double.parseDouble(point.getY())
                )
        );
    }
}
