package com.zelusik.eatery.dto.curation;

import com.zelusik.eatery.domain.curation.CurationElem;
import com.zelusik.eatery.dto.place.PlaceDto;
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

    public static CurationElemDto of(Long id, Long curationId, PlaceDto placeDto, CurationElemFileDto imageDto, LocalDateTime createdAt, LocalDateTime updatedAt) {
        return new CurationElemDto(id, curationId, placeDto, imageDto, createdAt, updatedAt);
    }

    public static CurationElemDto from(CurationElem entity) {
        return of(
                entity.getId(),
                entity.getCuration().getId(),
                PlaceDto.fromWithoutMarkedStatusAndImages(entity.getPlace()),
                CurationElemFileDto.from(entity.getImage()),
                entity.getCreatedAt(),
                entity.getUpdatedAt()
        );
    }
}
