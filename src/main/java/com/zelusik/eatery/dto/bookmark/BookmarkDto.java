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

    public static BookmarkDto of(Long id, Long memberId, Long placeId) {
        return new BookmarkDto(id, memberId, placeId);
    }

    public static BookmarkDto from(Bookmark entity) {
        return of(
                entity.getId(),
                entity.getMember().getId(),
                entity.getPlace().getId()
        );
    }
}
