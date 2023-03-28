package com.zelusik.eatery.app.dto.curation;

import com.zelusik.eatery.app.domain.curation.CurationElemFile;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDateTime;

@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Getter
public class CurationElemFileDto {

    private Long id;
    private String originalName;
    private String storedName;
    private String url;
    private String thumbnailStoredName;
    private String thumbnailUrl;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private LocalDateTime deletedAt;

    public static CurationElemFileDto of(Long id, String originalName, String storedName, String url, String thumbnailStoredName, String thumbnailUrl, LocalDateTime createdAt, LocalDateTime updatedAt, LocalDateTime deletedAt) {
        return new CurationElemFileDto(id, originalName, storedName, url, thumbnailStoredName, thumbnailUrl, createdAt, updatedAt, deletedAt);
    }

    public static CurationElemFileDto from(CurationElemFile entity) {
        return of(
                entity.getId(),
                entity.getOriginalName(),
                entity.getStoredName(),
                entity.getUrl(),
                entity.getThumbnailStoredName(),
                entity.getThumbnailUrl(),
                entity.getCreatedAt(),
                entity.getUpdatedAt(),
                entity.getDeletedAt()
        );
    }
}
