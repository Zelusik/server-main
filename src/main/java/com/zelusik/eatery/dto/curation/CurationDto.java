package com.zelusik.eatery.dto.curation;

import com.zelusik.eatery.domain.curation.Curation;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.List;

@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Getter
public class CurationDto {

    private Long id;
    private String title;
    private List<CurationElemDto> curationElemDtos;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public static CurationDto of(Long id, String title, List<CurationElemDto> curationElemDtos, LocalDateTime createdAt, LocalDateTime updatedAt) {
        return new CurationDto(id, title, curationElemDtos, createdAt, updatedAt);
    }

    public static CurationDto from(Curation entity) {
        return of(
                entity.getId(),
                entity.getTitle(),
                entity.getCurationElems().stream()
                        .map(CurationElemDto::from)
                        .toList(),
                entity.getCreatedAt(),
                entity.getUpdatedAt()
        );
    }
}
