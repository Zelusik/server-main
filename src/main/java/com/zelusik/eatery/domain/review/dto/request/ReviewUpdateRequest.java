package com.zelusik.eatery.domain.review.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;

import javax.validation.constraints.NotBlank;

@Getter
public class ReviewUpdateRequest {

    @Schema(description = "수정할 내용", example = "미래에 제가 살 곳은 여기로 정했습니다. 씹을때마다 ...")
    private String content;
}
