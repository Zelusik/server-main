package com.zelusik.eatery.dto;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * 기본적으로 redis caching을 위해 만든 dto class.
 * List는 역직렬화 문제가 있기 때문에 caching하기 위한 dto class로 사용하고자 한다.
 *
 * @reference https://bcp0109.tistory.com/384
 */
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PRIVATE)
@Getter
public class ListDto<T> {
    private List<T> content;
}
