package com.zelusik.eatery.controller;

import com.zelusik.eatery.constant.review.ReviewKeywordValue;
import com.zelusik.eatery.dto.SliceResponse;
import com.zelusik.eatery.dto.review.ReviewDto;
import com.zelusik.eatery.dto.review.request.ReviewCreateRequest;
import com.zelusik.eatery.dto.review.request.ReviewUpdateRequest;
import com.zelusik.eatery.dto.review.response.*;
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
import org.springframework.data.domain.Slice;
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
import java.util.List;

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
            summary = "리뷰 상세 조회",
            description = "리뷰 상세 정보를 단건 조회합니다.",
            security = @SecurityRequirement(name = "access-token")
    )
    @ApiResponses({
            @ApiResponse(description = "OK", responseCode = "200", content = @Content(schema = @Schema(implementation = FindReviewResponse.class))),
            @ApiResponse(description = "[3501] <code>reviewId</code>에 해당하는 리뷰를 찾을 수 없는 경우", responseCode = "404", content = @Content)
    })
    @GetMapping("/{reviewId}")
    public FindReviewResponse findReview(
            @AuthenticationPrincipal UserPrincipal userPrincipal,
            @Parameter(
                    description = "PK of review",
                    example = "2"
            ) @PathVariable Long reviewId
    ) {
        Long loginMemberId = userPrincipal.getMemberId();
        ReviewDto reviewDto = reviewService.findDtoById(loginMemberId, reviewId);
        return FindReviewResponse.from(reviewDto, loginMemberId);
    }

    @Operation(
            summary = "특정 가게의 리뷰 목록 조회",
            description = "특정 가게의 리뷰 목록을 조회합니다",
            security = @SecurityRequirement(name = "access-token")
    )
    @GetMapping
    public SliceResponse<FindReviewsForSpecificPlaceResponse> findReviewsForSpecificPlace(
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
        Slice<ReviewDto> reviewDtos = reviewService.findDtosByPlaceId(placeId, pageRequest);
        return new SliceResponse<FindReviewsForSpecificPlaceResponse>().from(reviewDtos.map(FindReviewsForSpecificPlaceResponse::from));
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
                .from(reviewService.findDtosOrderByCreatedAt(userPrincipal.getMemberId(), pageRequest).map(FeedResponse::from));
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
                    description = """ 
                            <p>리뷰 키워드. 목록은 다음과 같다.</p>
                            <p><strong>음식/가격 관련</strong></p>
                            <ul>
                               <li><code>FRESH</code>: 신선한 재료</li>
                               <li><code>BEST_FLAVOR</code>: 최고의 맛</li>
                               <li><code>BEST_MENU_COMBINATION</code>: 완벽한 메뉴 조합</li>
                               <li><code>LOCAL_FLAVOR</code>: 현지 느낌이 가득한</li>
                               <li><code>GOOD_PRICE</code>: 가성비가 좋은</li>
                               <li><code>GENEROUS_PORTIONS</code>: 넉넉한 양</li>
                            </ul>
                            <p><strong>분위기 관련</strong></p>
                            <ul>
                               <li><code>WITH_ALCOHOL</code>: 술과 함께하기 좋은</li>
                               <li><code>GOOD_FOR_DATE</code>: 데이트 하기에 좋은</li>
                               <li><code>WITH_ELDERS</code>: 웃어른과 함께하기 좋은</li>
                               <li><code>CAN_ALONE</code>: 혼밥 가능한</li>
                               <li><code>PERFECT_FOR_GROUP_MEETING</code>: 단체 모임에 좋은</li>
                               <li><code>WAITING</code>: 웨이팅 있는</li>
                               <li><code>SILENT</code>: 조용조용한</li>
                               <li><code>NOISY</code>: 왁자지껄한</li>
                            </ul>
                            """,
                    example = "NOISY"
            ) @RequestParam @NotEmpty List<ReviewKeywordValue> placeKeywords,
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

        List<List<String>> parsedMenuKeywords = menuKeywords.stream()
                .map(keywords -> Arrays.asList(keywords.split("/+")))
                .toList();

        String result = openAIService.getAutoCreatedReviewContent(
                placeKeywords.stream().map(ReviewKeywordValue::getDescription).toList(),
                menus,
                parsedMenuKeywords
        );
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
