package com.zelusik.eatery.domain.bookmark.dto;

import com.zelusik.eatery.domain.bookmark.entity.Bookmark;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PRIVATE)
@Getter
public class BookmarkDto {

    private Long id;
    private Long memberId;
    private Long placeId;

    public static BookmarkDto from(Bookmark entity) {
        return new BookmarkDto(
                entity.getId(),
                entity.getMember().getId(),
                entity.getPlace().getId()
        );
    }
}
