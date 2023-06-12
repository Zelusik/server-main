package com.zelusik.eatery.dto.curation.response;

import com.zelusik.eatery.dto.curation.CurationDto;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Getter
public class CurationResponse {

    @Schema(description = "PK", example = "1")
    private Long id;

    @Schema(description = "제목", example = "또간집 출연 맛집")
    private String title;

    @Schema(description = "콘텐츠 요소들")
    private List<CurationElemResponse> curationElems;

    public static CurationResponse of(Long id, String title, List<CurationElemResponse> curationElems) {
        return new CurationResponse(id, title, curationElems);
    }

    public static CurationResponse from(CurationDto dto) {
        return of(
                dto.getId(),
                dto.getTitle(),
                dto.getCurationElemDtos().stream()
                        .map(CurationElemResponse::from)
                        .toList()
        );
    }
}
