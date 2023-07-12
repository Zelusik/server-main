package com.zelusik.eatery.dto.curation.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

import javax.validation.constraints.NotBlank;

@AllArgsConstructor(access = AccessLevel.PRIVATE)
@NoArgsConstructor(access = AccessLevel.PRIVATE)
@Getter
public class CurationCreateRequest {

    @Schema(description = "제목", example = "또간집 출연 맛집")
    @NotBlank
    private String title;

    public static CurationCreateRequest of(String title) {
        return new CurationCreateRequest(title);
    }
}
