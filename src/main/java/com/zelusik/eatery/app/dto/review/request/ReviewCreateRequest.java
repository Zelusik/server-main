package com.zelusik.eatery.app.dto.review.request;

import com.zelusik.eatery.app.constant.review.ReviewKeyword;
import com.zelusik.eatery.app.domain.place.Place;
import com.zelusik.eatery.app.dto.place.PlaceDto;
import com.zelusik.eatery.app.dto.place.request.PlaceRequest;
import com.zelusik.eatery.app.dto.review.ReviewDtoWithMemberAndPlace;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.constraints.NotNull;
import java.util.List;

@AllArgsConstructor(access = AccessLevel.PRIVATE)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Setter
@Getter
public class ReviewCreateRequest {

    @Schema(description = "리뷰를 작성하고자 하는 장소 정보")
    private PlaceRequest place;

    @ArraySchema(arraySchema = @Schema(description = "키워드 목록", example = "[\"신선한 재료\", \"최고의 맛\"]"))
    @NotNull
    private List<String> keywords;

    @Schema(description = "자동으로 생성된 내용", example = "미래에 제가 살 곳은 여기로 정했습니다. 씹을 때마다...")
    private String autoCreatedContent;

    @Schema(description = "업로드할 내용", example = "미래에 제가 살 곳은 여기로 정했습니다. 고기를 주문하면 ...")
    private String content;

    @Schema(description = "업로드할 이미지 파일들")
    private List<MultipartFile> files;

    public static ReviewCreateRequest of(PlaceRequest place, List<String> keywords, String autoCreatedContent, String content, List<MultipartFile> files) {
        return new ReviewCreateRequest(place, keywords, autoCreatedContent, content, files);
    }

    public ReviewDtoWithMemberAndPlace toDto(Place place) {
        return toDto(PlaceDto.from(place));
    }

    public ReviewDtoWithMemberAndPlace toDto(PlaceDto placeDto) {
        return ReviewDtoWithMemberAndPlace.of(
                placeDto,
                this.getKeywords().stream()
                        .map(ReviewKeyword::valueOfDescription)
                        .toList(),
                this.getAutoCreatedContent(),
                this.getContent()
        );
    }
}
