package com.zelusik.eatery.domain.bookmark.api;

import com.zelusik.eatery.domain.bookmark.dto.response.BookmarkResponse;
import com.zelusik.eatery.global.auth.UserPrincipal;
import com.zelusik.eatery.domain.bookmark.service.BookmarkCommandService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.net.URI;

import static com.zelusik.eatery.global.common.constant.EateryConstants.API_MINOR_VERSION_HEADER_NAME;

@Tag(name = "북마크 API")
@RequiredArgsConstructor
@RequestMapping("/api")
@RestController
public class BookmarkController {

    private final BookmarkCommandService bookmarkCommandService;

    @Operation(
            summary = "장소 북마크",
            description = "<p><strong>Latest version: v1.1</strong>" +
                          "<p>특정 장소를 북마크에 저장한다.",
            security = @SecurityRequirement(name = "access-token")
    )
    @ApiResponses({
            @ApiResponse(description = "Created", responseCode = "201"),
            @ApiResponse(description = "[4300] 이미 저장한 장소를 다시 북마크에 저장하고자 하는 경우", responseCode = "409", content = @Content)
    })
    @PostMapping(value = "/v1/bookmarks", headers = API_MINOR_VERSION_HEADER_NAME + "=1")
    public ResponseEntity<BookmarkResponse> markPlaceV1_1(
            @AuthenticationPrincipal UserPrincipal userPrincipal,
            @Parameter(
                    description = "북마크에 저장하고자 하는 장소의 PK",
                    example = "1"
            ) @RequestParam Long placeId
    ) {
        BookmarkResponse response = BookmarkResponse.from(bookmarkCommandService.mark(userPrincipal.getMemberId(), placeId));

        return ResponseEntity
                .created(URI.create("/api/v1/bookmarks/" + response.getId()))
                .body(response);
    }

    @Operation(
            summary = "장소 북마크 취소",
            description = "<p><strong>Latest version: v1.1</strong>" +
                          "<p>특정 장소에 대해 저장한 북마크를 취소합니다.",
            security = @SecurityRequirement(name = "access-token")
    )
    @ApiResponses({
            @ApiResponse(description = "OK", responseCode = "200", content = @Content),
            @ApiResponse(description = "[4301] 북마크에 저장하지 않은 장소인 경우.", responseCode = "404", content = @Content)
    })
    @DeleteMapping(value = "/v1/bookmarks", headers = API_MINOR_VERSION_HEADER_NAME + "=1")
    public void deletePlaceBookmarkV1_1(
            @AuthenticationPrincipal UserPrincipal userPrincipal,
            @Parameter(
                    description = "저장된 북마크를 삭제하고자 하는 장소의 PK",
                    example = "1"
            ) @RequestParam Long placeId
    ) {
        bookmarkCommandService.delete(userPrincipal.getMemberId(), placeId);
    }
}
