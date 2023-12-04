package com.zelusik.eatery.domain.menu_keyword.dto.response;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public class MenuKeywordListResponseList {

    private List<MenuKeywordResponse> menuKeywords;
}
