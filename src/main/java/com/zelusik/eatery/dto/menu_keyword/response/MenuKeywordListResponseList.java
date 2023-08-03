package com.zelusik.eatery.dto.menu_keyword.response;

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
