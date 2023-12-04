package com.zelusik.eatery.domain.review.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PRIVATE)
@Getter
public class GettingAutoCreatedReviewContentResponse {

    @Schema(description = "자동 생성된 리뷰", example = "이 음식점은 신선한 재료와 넉넉한 양, 술과 함께 데이트하기에 최고입니다. 메뉴 중 시금치카츠카레는 싱그러운 맛과 육즙 가득힌 매콤한 특징이 있습니다. 또한 버터치킨카레는 부드러운 맛과 촉촉한 특징이 있습니다.")
    private String content;
}
