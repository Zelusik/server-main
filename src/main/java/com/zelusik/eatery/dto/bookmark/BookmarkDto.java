package com.zelusik.eatery.dto.bookmark;

import com.zelusik.eatery.domain.Bookmark;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDateTime;

@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Getter
public class BookmarkDto {

    private Long id;
    private Long memberId;
    private Long placeId;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

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
