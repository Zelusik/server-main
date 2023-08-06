package com.zelusik.eatery.controller;

import com.zelusik.eatery.dto.SliceResponse;
import com.zelusik.eatery.dto.review.request.ReviewCreateRequest;
import com.zelusik.eatery.dto.review.request.ReviewUpdateRequest;
import com.zelusik.eatery.dto.review.response.FeedResponse;
import com.zelusik.eatery.dto.review.response.GettingAutoCreatedReviewContentResponse;
import com.zelusik.eatery.dto.review.response.ReviewListElemResponse;
import com.zelusik.eatery.dto.review.response.ReviewResponse;
import com.zelusik.eatery.exception.review.MismatchedMenuKeywordCountException;
import com.zelusik.eatery.security.UserPrincipal;
import com.zelusik.eatery.service.OpenAIService;
import com.zelusik.eatery.service.ReviewService;
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
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import java.net.URI;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Tag(name = "리뷰 관련 API")
@RequiredArgsConstructor
@Validated
@RequestMapping("/api/reviews")
@RestController
public class ReviewController {

    private final ReviewService reviewService;
    private final OpenAIService openAIService;

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
            @ApiResponse(description = "[1350] 장소에 대한 추가 정보를 스크래핑 할 Scraping server에서 에러가 발생한 경우.", responseCode = "500", content = @Content),
    })
    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ReviewResponse> create(
            @AuthenticationPrincipal UserPrincipal userPrincipal,
            @ParameterObject @Valid @ModelAttribute ReviewCreateRequest request
    ) {
        ReviewResponse response = ReviewResponse.from(reviewService.create(userPrincipal.getMemberId(), request));
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
                .from(reviewService.findDtosByPlaceId(placeId, pageRequest)
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
            @AuthenticationPrincipal UserPrincipal userPrincipal,
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
                .from(reviewService.findDtosOrderByCreatedAt(userPrincipal.getMemberId(), pageRequest)
                        .map(FeedResponse::from));
    }

    // TODO: 메뉴 태그 정보가 담긴 ReviewResponse를 응답 객체로 사용할 것인지 검토 필요
    @Operation(
            summary = "내가 작성한 리뷰 조회",
            description = "<p>내가 작성한 리뷰를 최신순으로 조회합니다.",
            security = @SecurityRequirement(name = "access-token")
    )
    @GetMapping("/me")
    public SliceResponse<ReviewResponse> searchOfMe(
            @AuthenticationPrincipal UserPrincipal userPrincipal,
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
                .from(reviewService.findDtosByWriterId(userPrincipal.getMemberId(), pageRequest)
                        .map(ReviewResponse::from));
    }

    @Operation(
            summary = "리뷰 내용 자동 생성",
            description = "사용자로부터 장소에 대한 키워드, 메뉴에 대한 키워드들을 받아 리뷰 내용을 자동으로 작성합니다.",
            security = @SecurityRequirement(name = "access-token")
    )
    @GetMapping(value = "/contents/auto-creations")
    public GettingAutoCreatedReviewContentResponse getAutoCreatedContentWithGpt(
            @Parameter(
                    description = "리뷰를 남기려고 하는 장소에 대해 사용자가 선택한 키워드 목록",
                    example = "[\"신선한 재료\", \"넉넉한 양\", \"술과 함께\", \"데이트에 최고\", \"웃어른과\"]"
            ) @RequestParam @NotEmpty List<@NotBlank String> placeKeywords,
            @Parameter(
                    description = "리뷰에 태그한 메뉴 목록",
                    example = "[\"시금치카츠카레\", \"버터치킨카레\"]"
            ) @RequestParam(required = false) List<@NotBlank String> menus,
            @Parameter(
                    description = "<p>메뉴에 해당하는 사용자가 선택한 키워드 목록" +
                            "<p><code>menus</code>에서 전달한 각 메뉴들과 대응되도록 순서를 일치해야 합니다." +
                            "<p>또한, 각 키워드는 \"+\"로 구분한다.",
                    example = "[\"싱그러운+육즙 가득한+살짝 매콤\", \"부드러운+촉촉한\"]"
            ) @RequestParam(required = false) List<@NotBlank String> menuKeywords
    ) {
        if (menus.size() != menuKeywords.size()) {
            throw new MismatchedMenuKeywordCountException(menus, menuKeywords);
        }

        Map<String, List<String>> menuKeywordMap = new LinkedHashMap<>();
        List<List<String>> parsedMenuKeywords = menuKeywords.stream()
                .map(keywords -> Arrays.asList(keywords.split("/+")))
                .toList();
        for (int i = 0; i < menus.size(); i++) {
            menuKeywordMap.put(menus.get(i), parsedMenuKeywords.get(i));
        }

        String result = openAIService.getAutoCreatedReviewContent(placeKeywords, menuKeywordMap);
        return new GettingAutoCreatedReviewContentResponse(result);
    }

    @Operation(
            summary = "리뷰 내용 수정",
            description = "<p>리뷰 내용을 수정합니다.",
            security = @SecurityRequirement(name = "access-token")
    )
    @ApiResponses({
            @ApiResponse(description = "OK", responseCode = "200", content = @Content(schema = @Schema(implementation = ReviewResponse.class))),
            @ApiResponse(description = "[3503] 리뷰 수정 권한이 없는 경우.", responseCode = "403", content = @Content)
    })
    @PatchMapping("/{reviewId}")
    public ReviewResponse update(
            @AuthenticationPrincipal UserPrincipal userPrincipal,
            @Parameter(
                    description = "수정할 리뷰의 PK",
                    example = "1"
            ) @PathVariable Long reviewId,
            @Valid @RequestBody ReviewUpdateRequest updateRequest
    ) {
        return ReviewResponse.from(reviewService.update(userPrincipal.getMemberId(), reviewId, updateRequest.getContent()));
    }

    @Operation(
            summary = "리뷰 삭제",
            description = "<p>리뷰를 삭제합니다.",
            security = @SecurityRequirement(name = "access-token")
    )
    @ApiResponses({
            @ApiResponse(description = "OK", responseCode = "200", content = @Content(schema = @Schema)),
            @ApiResponse(description = "[3502] 리뷰 삭제 권한이 없는 경우", responseCode = "403", content = @Content)
    })
    @DeleteMapping("/{reviewId}")
    public void delete(
            @AuthenticationPrincipal UserPrincipal userPrincipal,
            @Parameter(
                    description = "삭제할 리뷰의 PK",
                    example = "1"
            ) @PathVariable Long reviewId
    ) {
        reviewService.delete(userPrincipal.getMemberId(), reviewId);
    }
}
