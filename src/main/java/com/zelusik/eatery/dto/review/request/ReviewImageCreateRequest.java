package com.zelusik.eatery.dto.review.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.constraints.NotNull;

@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Setter
@Getter
public class ReviewImageCreateRequest {

    @Schema(description = "이미지")
    @NotNull
    private MultipartFile image;
}