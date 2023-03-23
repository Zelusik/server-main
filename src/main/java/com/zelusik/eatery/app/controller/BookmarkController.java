package com.zelusik.eatery.app.controller;

import com.zelusik.eatery.app.dto.bookmark.response.BookmarkResponse;
import com.zelusik.eatery.app.service.BookmarkService;
import com.zelusik.eatery.global.security.UserPrincipal;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.net.URI;

@Tag(name = "북마크 API")
@RequiredArgsConstructor
@RequestMapping("/api/bookmarks")
@RestController
public class BookmarkController {

    private final BookmarkService bookmarkService;

    @Operation(
            summary = "장소 북마크",
            description = "특정 장소를 북마크에 저장한다.",
            security = @SecurityRequirement(name = "access-token")
    )
    @PostMapping
    public ResponseEntity<BookmarkResponse> mark(
            @Parameter(hidden = true) @AuthenticationPrincipal UserPrincipal userPrincipal,
            @Parameter(
                    description = "북마크에 저장하고자 하는 장소의 PK",
                    example = "1"
            ) @RequestParam Long placeId
    ) {
        BookmarkResponse response = BookmarkResponse.from(bookmarkService.mark(userPrincipal.getMemberId(), placeId));

        return ResponseEntity
                .created(URI.create("/api/bookmarks"))
                .body(response);
    }
}
