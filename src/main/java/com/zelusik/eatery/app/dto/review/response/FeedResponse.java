package com.zelusik.eatery.app.dto.review.response;

import com.zelusik.eatery.app.constant.review.ReviewKeyword;
import com.zelusik.eatery.app.dto.member.response.MemberResponse;
import com.zelusik.eatery.app.dto.place.response.FeedPlaceResponse;
import com.zelusik.eatery.app.dto.review.ReviewDtoWithMemberAndPlace;
import com.zelusik.eatery.app.dto.review.ReviewFileDto;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Getter
public class FeedResponse {

    @Schema(description = "리뷰 id(PK)", example = "1")
    private Long id;

    @Schema(description = "리뷰를 작성한 회원 정보")
    private MemberResponse writer;

    @Schema(description = "리뷰가 작성된 가게 정보")
    private FeedPlaceResponse place;

    @ArraySchema(arraySchema = @Schema(description = "키워드 목록", example = "[\"신선한 재료\", \"최고의 맛\"]"))
    private List<String> keywords;

    @Schema(description = "내용", example = "미래에 제가 살 곳은 여기로 정했습니다. 고기를 주문하면 ...")
    private String content;

    @Schema(description = "리뷰에 첨부된 이미지 파일 목록")
    private List<String> reviewFiles;

    public static FeedResponse of(Long id, MemberResponse writer, FeedPlaceResponse place, List<String> keywords, String content, List<String> reviewFiles) {
        return new FeedResponse(id, writer, place, keywords, content, reviewFiles);
    }

    public static FeedResponse from(ReviewDtoWithMemberAndPlace dto) {
        return of(
                dto.id(),
                MemberResponse.from(dto.writerDto()),
                FeedPlaceResponse.from(dto.placeDto()),
                dto.keywords().stream()
                        .map(ReviewKeyword::getDescription)
                        .toList(),
                dto.content(),
                dto.reviewFileDtos().stream()
                        .map(ReviewFileDto::url)
                        .toList()
        );
    }
}