package com.zelusik.eatery.dto.review.response;

import com.zelusik.eatery.dto.review.ReviewImageDto;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.lang.Nullable;

import java.util.List;
import java.util.Optional;

@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PRIVATE)
@Getter
public class ReviewImageResponse {

    @Schema(description = "이미지 url", example = "https://...")
    private String url;

    @Schema(description = "썸네일 이미지 url", example = "https://...")
    private String thumbnailUrl;

    @Schema(description = "이미지에 생성된 메뉴 태그 목록")
    private List<ReviewImageMenuTagResponse> menuTags;

    public static ReviewImageResponse from(ReviewImageDto dto) {
        return new ReviewImageResponse(
                dto.getUrl(),
                dto.getThumbnailUrl(),
                Optional.ofNullable(dto.getMenuTags())
                        .map(menuTags -> menuTags.stream()
                                .map(ReviewImageMenuTagResponse::from)
                                .toList())
                        .orElse(List.of())
        );
    }
}
