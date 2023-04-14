package com.zelusik.eatery.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.Sort;

import java.util.List;

@AllArgsConstructor(access = AccessLevel.PRIVATE)
@NoArgsConstructor
@Getter
public class SliceResponse<T> {

    @Schema(description = "현재 page 번호", example = "0")
    private Integer number;

    @Schema(description = "현재 page의 크기", example = "15")
    private Integer size;

    @Schema(description = "현재 page에 포함된 데이터 개수", example = "12")
    private Integer numOfElements;

    @Schema(description = "응답 데이터 리스트")
    private List<T> contents;

    @Schema(description = "응답 데이터 존재 여부", example = "true")
    private Boolean hasContent;

    @Schema(description = "정렬 정보")
    private Sort sort;

    @Schema(description = "첫번째 페이지인지", example = "false")
    private Boolean isFirst;

    @Schema(description = "마지막 페이지인지", example = "true")
    private Boolean isLast;

    @Schema(description = "다음 페이지 존재 여부", example = "false")
    private Boolean hasNext;

    @Schema(description = "이전 페이지 존재 여부", example = "true")
    private Boolean hasPrevious;

    public SliceResponse<T> from(Slice<T> slice) {
        return new SliceResponse<T>(
                slice.getNumber(),
                slice.getSize(),
                slice.getNumberOfElements(),
                slice.getContent(),
                slice.hasContent(),
                slice.getSort(),
                slice.isFirst(),
                slice.isLast(),
                slice.hasNext(),
                slice.hasPrevious()
        );
    }
}
