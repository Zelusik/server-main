package com.zelusik.eatery.app.controller;

import com.zelusik.eatery.app.dto.SliceResponse;
import com.zelusik.eatery.app.dto.review.request.ReviewCreateRequest;
import com.zelusik.eatery.app.dto.review.response.FeedResponse;
import com.zelusik.eatery.app.dto.review.response.ReviewListElemResponse;
import com.zelusik.eatery.app.dto.review.response.ReviewResponse;
import com.zelusik.eatery.app.service.ReviewService;
import com.zelusik.eatery.global.security.UserPrincipal;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springdoc.api.annotations.ParameterObject;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.net.URI;

@Tag(name = "리뷰 관련 API")
@RequiredArgsConstructor
@RequestMapping("/api/reviews")
@RestController
public class ReviewController {

    private final ReviewService reviewService;

    @Operation(
            summary = "리뷰 생성",
            description = "<p>리뷰 내용과 장소 정보를 받아 리뷰를 생성합니다.</p>" +
                    "<p>영업시간, SNS 주소 등 추가로 필요한 정보는 상세 페이지(<code>place.pageUrl</code>)에서 받아옵니다.</p>" +
                    "<p>요청 시 <strong>multipart/form-data</strong> content-type으로 요쳥해야 합니다.",
            security = @SecurityRequirement(name = "access-token")
    )
    @ApiResponses({
            @ApiResponse(description = "Created", responseCode = "201", content = @Content(schema = @Schema(implementation = ReviewResponse.class))),
            @ApiResponse(description = "[1001] 전달받은 파일을 읽을 수 없는 경우.", responseCode = "400", content = @Content),
            @ApiResponse(description = "[1350] 장소에 대한 추가 정보를 스크래핑 할 Flask 서버에서 에러가 발생한 경우.", responseCode = "500", content = @Content),
            @ApiResponse(description = "[3000] 상세 페이지에서 읽어온 가게 영업시간이 처리할 수 없는 형태일 경우.", responseCode = "500", content = @Content)
    })
    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ReviewResponse> create(
            @Parameter(hidden = true) @AuthenticationPrincipal UserPrincipal userPrincipal,
            @ParameterObject @Valid @ModelAttribute ReviewCreateRequest request
    ) {
        ReviewResponse response = ReviewResponse.from(reviewService.create(userPrincipal.getMemberId(), request, request.getFiles()));

        return ResponseEntity
                .created(URI.create("/api/reviews/" + response.getId()))
                .body(response);
    }

    @Operation(
            summary = "특정 가게의 리뷰 목록 조회.",
            description = "<p>특정 가게의 리뷰 목록을 조회합니다.</p>" +
                    "<p>가장 많이 태그된 세 개의 키워드 응답 미구현(추후 구현 예정)</p>",
            security = @SecurityRequirement(name = "access-token")
    )
    @GetMapping
    public SliceResponse<ReviewListElemResponse> searchOfPlace(
            @Parameter(
                    description = "리뷰를 조회하고자 하는 가게의 id(PK)",
                    example = "1"
            ) @RequestParam Long placeId,
            @Parameter(
                    description = "페이지 번호 (0부터 시작합니다). 기본값은 0입니다.",
                    example = "0"
            ) @RequestParam(required = false, defaultValue = "0") int page,
            @Parameter(
                    description = "한 페이지에 담긴 데이터의 최대 개수(사이즈). 기본값은 15입니다.",
                    example = "15"
            ) @RequestParam(required = false, defaultValue = "15") int size
    ) {
        PageRequest pageRequest = PageRequest.of(page, size, Sort.by("createdAt").descending());
        return new SliceResponse<ReviewListElemResponse>()
                .from(reviewService.searchDtosByPlaceId(placeId, pageRequest)
                        .map(ReviewListElemResponse::from));
    }

    @Operation(
            summary = "피드 조회",
            description = "<p>피드에 보여줄 리뷰 목록을 조회합니다." +
                    "<p>모든 리뷰를 최신순으로 보여줍니다." +
                    "<p>추후 기획 단계에서 고안된 알고리즘에 의해 정렬하여 제공할 예정입니다. (현재는 미구현) 다만, 요청/응답 데이터의 변경은 없을 예정",
            security = @SecurityRequirement(name = "access-token")
    )
    @GetMapping("/feed")
    public SliceResponse<FeedResponse> searchFeed(
            @Parameter(hidden = true) @AuthenticationPrincipal UserPrincipal userPrincipal,
            @Parameter(
                    description = "페이지 번호 (0부터 시작합니다). 기본값은 0입니다.",
                    example = "0"
            ) @RequestParam(required = false, defaultValue = "0") int page,
            @Parameter(
                    description = "한 페이지에 담긴 데이터의 최대 개수(사이즈). 기본값은 15입니다.",
                    example = "15"
            ) @RequestParam(required = false, defaultValue = "15") int size
    ) {
        PageRequest pageRequest = PageRequest.of(page, size, Sort.by("createdAt").descending());
        return new SliceResponse<FeedResponse>()
                .from(reviewService.searchDtosOrderByCreatedAt(pageRequest)
                        .map(FeedResponse::from));
    }

    @Operation(
            summary = "피드 조회",
            description = "<p>내가 작성한 리뷰를 최신순으로 조회합니다.",
            security = @SecurityRequirement(name = "access-token")
    )
    @GetMapping("/me")
    public SliceResponse<ReviewResponse> searchOfMe(
            @Parameter(hidden = true) @AuthenticationPrincipal UserPrincipal userPrincipal,
            @Parameter(
                    description = "페이지 번호 (0부터 시작합니다). 기본값은 0입니다.",
                    example = "0"
            ) @RequestParam(required = false, defaultValue = "0") int page,
            @Parameter(
                    description = "한 페이지에 담긴 데이터의 최대 개수(사이즈). 기본값은 15입니다.",
                    example = "15"
            ) @RequestParam(required = false, defaultValue = "15") int size
    ) {
        PageRequest pageRequest = PageRequest.of(page, size, Sort.by("createdAt").descending());
        return new SliceResponse<ReviewResponse>()
                .from(reviewService.searchDtosByWriterId(userPrincipal.getMemberId(), pageRequest)
                        .map(ReviewResponse::from));
    }
}
