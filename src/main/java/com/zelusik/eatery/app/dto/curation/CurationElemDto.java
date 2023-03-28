package com.zelusik.eatery.app.dto.curation;

import com.zelusik.eatery.app.domain.curation.CurationElem;
import com.zelusik.eatery.app.dto.place.PlaceDto;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDateTime;

@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Getter
public class CurationElemDto {

    private Long id;
    private Long curationId;
    private PlaceDto placeDto;
    private CurationElemFileDto imageDto;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private LocalDateTime deletedAt;

    public static CurationElemDto of(Long id, Long curationId, PlaceDto placeDto, CurationElemFileDto imageDto, LocalDateTime createdAt, LocalDateTime updatedAt, LocalDateTime deletedAt) {
        return new CurationElemDto(id, curationId, placeDto, imageDto, createdAt, updatedAt, deletedAt);
    }

    public static CurationElemDto from(CurationElem entity) {
        return of(
                entity.getId(),
                entity.getCuration().getId(),
                PlaceDto.from(entity.getPlace(), null),
                CurationElemFileDto.from(entity.getImage()),
                entity.getCreatedAt(),
                entity.getUpdatedAt(),
                entity.getDeletedAt()
        );
    }
}
