package com.zelusik.eatery.app.dto.curation;

import com.zelusik.eatery.app.domain.curation.CurationElem;
import com.zelusik.eatery.app.dto.place.PlaceDto;

import java.time.LocalDateTime;

public record CurationElemDto(
        Long id,
        Long curationId,
        PlaceDto placeDto,
        CurationElemFileDto imageDto,
        LocalDateTime createdAt,
        LocalDateTime updatedAt,
        LocalDateTime deletedAt
) {
    public static CurationElemDto of(Long id, Long curationId, PlaceDto placeDto, CurationElemFileDto imageDto, LocalDateTime createdAt, LocalDateTime updatedAt, LocalDateTime deletedAt) {
        return new CurationElemDto(id, curationId, placeDto, imageDto, createdAt, updatedAt, deletedAt);
    }

    public static CurationElemDto from(CurationElem entity) {
        return of(
                entity.getId(),
                entity.getCuration().getId(),
                PlaceDto.from(entity.getPlace()),
                CurationElemFileDto.from(entity.getImage()),
                entity.getCreatedAt(),
                entity.getUpdatedAt(),
                entity.getDeletedAt()
        );
    }
}
