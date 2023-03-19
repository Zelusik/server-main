package com.zelusik.eatery.app.dto.curation;

import com.zelusik.eatery.app.domain.curation.Curation;

import java.time.LocalDateTime;
import java.util.List;

public record CurationDto(
        Long id,
        String title,
        List<CurationElemDto> curationElemDtos,
        LocalDateTime createdAt,
        LocalDateTime updatedAt,
        LocalDateTime deletedAt
) {
    public static CurationDto of(Long id, String title, List<CurationElemDto> curationElemDtos, LocalDateTime createdAt, LocalDateTime updatedAt, LocalDateTime deletedAt) {
        return new CurationDto(id, title, curationElemDtos, createdAt, updatedAt, deletedAt);
    }

    public static CurationDto from(Curation entity) {
        return of(
                entity.getId(),
                entity.getTitle(),
                entity.getCurationElems().stream()
                        .map(CurationElemDto::from)
                        .toList(),
                entity.getCreatedAt(),
                entity.getUpdatedAt(),
                entity.getDeletedAt()
        );
    }
}
