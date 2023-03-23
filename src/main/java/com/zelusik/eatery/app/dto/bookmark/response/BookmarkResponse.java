package com.zelusik.eatery.app.dto.bookmark.response;

import com.zelusik.eatery.app.dto.bookmark.BookmarkDto;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Getter
public class BookmarkResponse {

    @Schema(description = "북마크 PK", example = "1")
    private Long id;

    @Schema(description = "북마크한 회원의 PK", example = "2")
    private Long memberId;

    @Schema(description = "북마크한 장소의 PK", example = "3")
    private Long placeId;

    public static BookmarkResponse of(Long id, Long memberId, Long placeId) {
        return new BookmarkResponse(id, memberId, placeId);
    }

    public static BookmarkResponse from(BookmarkDto bookmarkDto) {
        return of(
                bookmarkDto.id(),
                bookmarkDto.memberId(),
                bookmarkDto.placeId()
        );
    }
}
