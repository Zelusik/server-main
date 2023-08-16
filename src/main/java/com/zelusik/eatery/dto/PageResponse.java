package com.zelusik.eatery.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.domain.Page;

import java.util.List;

@AllArgsConstructor(access = AccessLevel.PRIVATE)
@NoArgsConstructor
@Getter
public class PageResponse<T> {

    @Schema(description = "전체 page 수", example = "6")
    private Integer totalPages;

    @Schema(description = "조회된 전체 데이터 수", example = "155")
    private Long totalElements;

    @Schema(description = "첫 번째 page인지", example = "false")
    private Boolean isFirst;

    @Schema(description = "마지막 page인지", example = "true")
    private Boolean isLast;

    @Schema(description = "현재 page의 크기", example = "15")
    private Integer size;

    @Schema(description = "현재 page 번호", example = "0")
    private Integer number;

    @Schema(description = "응답 데이터 리스트")
    private List<T> contents;

    @Schema(description = "현재 page에 포함된 데이터 개수", example = "30")
    private Integer numOfElements;

    @Schema(description = "응답 데이터가 비어있는지", example = "false")
    private Boolean isEmpty;

    public PageResponse<T> from(Page<T> page) {
        return new PageResponse<>(
                page.getTotalPages(),
                page.getTotalElements(),
                page.isFirst(),
                page.isLast(),
                page.getSize(),
                page.getNumber(),
                page.getContent(),
                page.getNumberOfElements(),
                page.isEmpty()
        );
    }
}
