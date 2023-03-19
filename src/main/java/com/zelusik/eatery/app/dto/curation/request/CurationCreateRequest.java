package com.zelusik.eatery.app.dto.curation.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

import javax.validation.constraints.NotBlank;

@Getter
public class CurationCreateRequest {

    @Schema(description = "제목", example = "또간집 출연 맛집")
    @NotBlank
    private String title;
}
