package com.zelusik.eatery.app.dto.review.request;

import com.zelusik.eatery.app.constant.review.ReviewKeywordValue;
import com.zelusik.eatery.app.dto.ImageDto;
import com.zelusik.eatery.app.dto.place.PlaceDto;
import com.zelusik.eatery.app.dto.place.request.PlaceCreateRequest;
import com.zelusik.eatery.app.dto.review.ReviewDtoWithMemberAndPlace;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import java.util.List;

@AllArgsConstructor(access = AccessLevel.PRIVATE)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Setter
@Getter
public class ReviewCreateRequest {

    @Schema(description = "리뷰를 작성하고자 하는 장소 정보")
    private PlaceCreateRequest place;

    @Schema(description = "키워드 목록", example = "[\"신선한 재료\", \"최고의 맛\"]")
    @NotEmpty
    private List<@NotBlank String> keywords;

    @Schema(description = "자동으로 생성된 내용", example = "미래에 제가 살 곳은 여기로 정했습니다. 씹을 때마다...")
    private String autoCreatedContent;

    @Schema(description = "업로드할 내용", example = "미래에 제가 살 곳은 여기로 정했습니다. 고기를 주문하면 ...")
    private String content;

    @Schema(description = "업로드할 이미지 파일들")
    private List<ImageDto> images;

    public static ReviewCreateRequest of(PlaceCreateRequest place, List<String> keywords, String autoCreatedContent, String content, List<ImageDto> images) {
        return new ReviewCreateRequest(place, keywords, autoCreatedContent, content, images);
    }

    public ReviewDtoWithMemberAndPlace toDto(PlaceDto placeDto) {
        return ReviewDtoWithMemberAndPlace.of(
                placeDto,
                this.getKeywords().stream()
                        .map(ReviewKeywordValue::valueOfDescription)
                        .toList(),
                this.getAutoCreatedContent(),
                this.getContent()
        );
    }
}
