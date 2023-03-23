package com.zelusik.eatery.app.dto.bookmark;

import com.zelusik.eatery.app.domain.Bookmark;

import java.time.LocalDateTime;

public record BookmarkDto(
        Long id,
        Long memberId,
        Long placeId,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {
    public static BookmarkDto of(Long id, Long memberId, Long placeId, LocalDateTime createdAt, LocalDateTime updatedAt) {
        return new BookmarkDto(id, memberId, placeId, createdAt, updatedAt);
    }

    public static BookmarkDto from(Bookmark entity) {
        return of(
                entity.getId(),
                entity.getMember().getId(),
                entity.getPlace().getId(),
                entity.getCreatedAt(),
                entity.getUpdatedAt()
        );
    }
}
