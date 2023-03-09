package com.zelusik.eatery.app.domain.constant;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum ReviewKeyword {

    FRESH("신선한 재료"),
    BEST_FLAVOR("최고의 맛"),
    BEST_MENU_COMBINATION("완벽 메뉴조합"),
    GOOD_PRICE("가성비 갑"),
    GOOD_FOR_BLIND_DATE("소개팅에 최고"),
    WITH_ELDERS("웃어른과"),
    PERFECT_FOR_GROUP_MEETING("단체모임에 딱"),
    CAN_ALONE("혼밥 가능"),
    WAITING("웨이팅 있음"),
    SILENT("조용조용한"),
    NOISY("왁자지껄한"),
    ;

    private final String description;
}
