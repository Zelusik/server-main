package com.zelusik.eatery.app.dto.curation;

import com.zelusik.eatery.app.domain.curation.CurationElemFile;

import java.time.LocalDateTime;

public record CurationElemFileDto(
        Long id,
        String originalName,
        String storedName,
        String url,
        LocalDateTime createdAt,
        LocalDateTime updatedAt,
        LocalDateTime deletedAt
) {
    public static CurationElemFileDto of(Long id, String originalName, String storedName, String url, LocalDateTime createdAt, LocalDateTime updatedAt, LocalDateTime deletedAt) {
        return new CurationElemFileDto(id, originalName, storedName, url, createdAt, updatedAt, deletedAt);
    }

    public static CurationElemFileDto from(CurationElemFile entity) {
        return of(
                entity.getId(),
                entity.getOriginalName(),
                entity.getStoredName(),
                entity.getUrl(),
                entity.getCreatedAt(),
                entity.getUpdatedAt(),
                entity.getDeletedAt()
        );
    }
}
