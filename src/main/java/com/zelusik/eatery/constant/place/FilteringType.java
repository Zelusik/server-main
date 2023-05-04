package com.zelusik.eatery.constant.place;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "<p>저장된 장소에 대한 filtering keyword의 유형." +
        "<ul>" +
        "<li><code>FIRST_CATEGORY</code>: 음식 카테고리(first category). 한식, 일식 등)</li>" +
        "<li><code>SECOND_CATEGORY</code>: 음식 카테고리(second category) 햄버거, 피자, 국밥 등</li>" +
        "<li><code>TOP_3_KEYWORDS</code>: 장소의 top 3 keyword</li>" +
        "<li><code>ADDRESS</code>: 장소의 주소 (ex. 영통구, 연남동 등)</li>" +
        "</ul>",
        example = "ADDRESS")
public enum FilteringType {
    FIRST_CATEGORY,
    SECOND_CATEGORY,
    TOP_3_KEYWORDS,
    ADDRESS,
    NONE,
}
