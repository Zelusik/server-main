package com.zelusik.eatery.app.dto.review.request;

import com.zelusik.eatery.app.domain.constant.ReviewKeyword;
import com.zelusik.eatery.app.domain.place.Place;
import com.zelusik.eatery.app.dto.place.PlaceDto;
import com.zelusik.eatery.app.dto.place.request.PlaceRequest;
import com.zelusik.eatery.app.dto.review.ReviewDto;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.NotNull;
import java.util.List;

@AllArgsConstructor
@Getter
public class ReviewCreateRequest {

    @Schema(description = "리뷰를 작성하고자 하는 장소 정보")
    private PlaceRequest place;

    @Schema(description = "키워드 목록")
    @NotNull
    private List<ReviewKeyword> keywords;

    @Schema(description = "자동으로 생성된 내용", example = "미래에 제가 살 곳은 여기로 정했습니다. 씹을 때마다...")
    @Length(min = 1, max = 400)
    private String autoCreatedContent;

    @Schema(description = "업로드할 내용", example = "미래에 제가 살 곳은 여기로 정했습니다. 고기를 주문하면 ...")
    @Length(min = 1, max = 400)
    private String content;

    public ReviewDto toDto(Place place) {
        return toDto(PlaceDto.from(place));
    }

    public ReviewDto toDto(PlaceDto placeDto) {
        return ReviewDto.of(
                placeDto,
                this.getKeywords(),
                this.getAutoCreatedContent(),
                this.getContent()
        );
    }
}
