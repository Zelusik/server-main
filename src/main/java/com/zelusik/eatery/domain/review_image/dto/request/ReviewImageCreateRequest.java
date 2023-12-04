package com.zelusik.eatery.domain.review_image.dto.request;

import com.zelusik.eatery.domain.review_image_menu_tag.dto.request.ReviewMenuTagCreateRequest;
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