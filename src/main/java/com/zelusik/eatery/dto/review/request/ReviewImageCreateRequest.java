package com.zelusik.eatery.dto.review.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;
import org.springframework.lang.Nullable;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.constraints.NotNull;
import java.util.List;

@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Setter // for @ModelAttribute
@Getter
public class ReviewImageCreateRequest {

    @Schema(description = "리뷰 이미지")
    @NotNull
    private MultipartFile image;

    @Nullable
    private List<ReviewMenuTagCreateRequest> menuTags;
}