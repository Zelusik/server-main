package com.zelusik.eatery.constant.place;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "<p>저장된 장소에 대한 filtering keyword의 유형." +
        "<ul>" +
        "<li><code>CATEGORY</code>: 음식 카테고리 (second category)</li>" +
        "<li><code>TOP_3_KEYWORDS</code>: 장소의 top 3 keyword</li>" +
        "<li><code>ADDRESS</code>: 장소의 주소 (ex. 영통구, 연남동 등)</li>" +
        "</ul>",
        example = "ADDRESS")
public enum FilteringType {
    CATEGORY, TOP_3_KEYWORDS, ADDRESS, NONE
}
