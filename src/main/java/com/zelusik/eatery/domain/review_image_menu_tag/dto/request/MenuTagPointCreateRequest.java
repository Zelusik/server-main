package com.zelusik.eatery.domain.review_image_menu_tag.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

import javax.persistence.Column;
import javax.validation.constraints.NotBlank;

@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Setter // for @ModelAttribute
@Getter
public class MenuTagPointCreateRequest {

    @Schema(description = "메뉴 태그의 x좌표", example = "30.45")
    @NotBlank
    @Column(nullable = false)
    private String x;

    @Schema(description = "메뉴 태그의 y좌표", example = "12.7504")
    @NotBlank
    @Column(nullable = false)
    private String y;
}
