package com.zelusik.eatery.dto.curation;

import com.zelusik.eatery.domain.curation.CurationElem;
import com.zelusik.eatery.dto.place.PlaceDtoWithMarkedStatus;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDateTime;

@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Getter
public class CurationElemDto {

    private Long id;
    private Long curationId;
    private PlaceDtoWithMarkedStatus placeDtoWithMarkedStatus;
    private CurationElemFileDto imageDto;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public static CurationElemDto of(Long id, Long curationId, PlaceDtoWithMarkedStatus placeDtoWithMarkedStatus, CurationElemFileDto imageDto, LocalDateTime createdAt, LocalDateTime updatedAt) {
        return new CurationElemDto(id, curationId, placeDtoWithMarkedStatus, imageDto, createdAt, updatedAt);
    }

    public static CurationElemDto from(CurationElem entity) {
        return of(
                entity.getId(),
                entity.getCuration().getId(),
                PlaceDtoWithMarkedStatus.from(entity.getPlace(), null),
                CurationElemFileDto.from(entity.getImage()),
                entity.getCreatedAt(),
                entity.getUpdatedAt()
        );
    }
}
