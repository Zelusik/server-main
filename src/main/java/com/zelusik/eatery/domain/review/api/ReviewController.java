package com.zelusik.eatery.domain.review.api;

import com.zelusik.eatery.domain.review.constant.ReviewEmbedOption;
import com.zelusik.eatery.domain.review.constant.ReviewKeywordValue;
import com.zelusik.eatery.domain.review.dto.ReviewWithPlaceMarkedStatusDto;
import com.zelusik.eatery.domain.review.dto.request.ReviewCreateRequest;
import com.zelusik.eatery.domain.review.dto.request.ReviewUpdateRequest;
import com.zelusik.eatery.domain.review.dto.response.*;
import com.zelusik.eatery.domain.review.exception.MismatchedMenuKeywordCountException;
import com.zelusik.eatery.domain.review.service.ReviewCommandService;
import com.zelusik.eatery.domain.review.service.ReviewQueryService;
import com.zelusik.eatery.global.common.dto.response.SliceResponse;
import com.zelusik.eatery.global.open_ai.service.OpenAIService;
import com.zelusik.eatery.global.security.UserPrincipal;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springdoc.api.annotations.ParameterObject;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Slice;
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

import static com.zelusik.eatery.domain.review.constant.ReviewEmbedOption.PLACE;
import static com.zelusik.eatery.global.common.constant.EateryConstants.API_MINOR_VERSION_HEADER_NAME;

@Tag(name = "리뷰 관련 API")
@RequiredArgsConstructor
@Validated
@RequestMapping("/api")
@RestController
public class ReviewController {

    private final ReviewCommandService reviewCommandService;
    private final ReviewQueryService reviewQueryService;
    private final OpenAIService openAIService;

    @Operation(
            summary = "리뷰 생성",
            description = "<p><strong>Latest version: v1.1</strong>" +
                          "<p>리뷰 내용과 장소 정보를 받아 리뷰를 생성합니다.</p>" +
                          "<p>영업시간, SNS 주소 등 추가로 필요한 정보는 상세 페이지(<code>place.pageUrl</code>)에서 받아옵니다.</p>" +
                          "<p>요청 시 <strong>multipart/form-data</strong> content-type으로 요쳥해야 합니다.",
            security = @SecurityRequirement(name = "access-token")
    )
    @ApiResponses({
            @ApiResponse(description = "Created", responseCode = "201"),
            @ApiResponse(description = "[1001] 전달받은 파일을 읽을 수 없는 경우.", responseCode = "400", content = @Content),
            @ApiResponse(description = "[1350] 장소에 대한 추가 정보를 스크래핑 할 Scraping server에서 에러가 발생한 경우.", responseCode = "500", content = @Content),
    })
    @PostMapping(value = "/v1/reviews", headers = API_MINOR_VERSION_HEADER_NAME + "=1", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ReviewResponse> createV1_1(
            @AuthenticationPrincipal UserPrincipal userPrincipal,
            @ParameterObject @Valid @ModelAttribute ReviewCreateRequest request
    ) {
        ReviewWithPlaceMarkedStatusDto reviewDto = reviewCommandService.create(userPrincipal.getMemberId(), request);
        return ResponseEntity
                .created(URI.create("/api/reviews/" + reviewDto.getId()))
                .body(ReviewResponse.from(reviewDto));
    }

    @Operation(
            summary = "리뷰 상세 조회",
            description = "<p><strong>Latest version: v1.1</strong>" +
                          "<p>리뷰 상세 정보를 단건 조회합니다.",
            security = @SecurityRequirement(name = "access-token")
    )
    @ApiResponses({
            @ApiResponse(description = "OK", responseCode = "200"),
            @ApiResponse(description = "[3501] <code>reviewId</code>에 해당하는 리뷰를 찾을 수 없는 경우", responseCode = "404", content = @Content)
    })
    @GetMapping(value = "/v1/reviews/{reviewId}", headers = API_MINOR_VERSION_HEADER_NAME + "=1")
    public FindReviewResponse findReviewV1_1(
            @AuthenticationPrincipal UserPrincipal userPrincipal,
            @Parameter(
                    description = "PK of review",
                    example = "2"
            ) @PathVariable Long reviewId
    ) {
        Long loginMemberId = userPrincipal.getMemberId();
        ReviewWithPlaceMarkedStatusDto reviewWithPlaceMarkedStatusDto = reviewQueryService.findDtoById(loginMemberId, reviewId);
        return FindReviewResponse.from(reviewWithPlaceMarkedStatusDto, loginMemberId);
    }

    @Operation(
            summary = "리뷰 목록 조회",
            description = "<p><strong>Latest version: v1.1</strong>" +
                          "<p>리뷰 목록을 조회합니다",
            security = @SecurityRequirement(name = "access-token")
    )
    @GetMapping(value = "/v1/reviews", headers = API_MINOR_VERSION_HEADER_NAME + "=1")
    public SliceResponse<FindReviewsResponse> findReviewsV1_1(
            @AuthenticationPrincipal UserPrincipal userPrincipal,
            @Parameter(
                    description = "필터 - 특정 회원이 작성한 리뷰만 조회",
                    example = "1"
            ) @RequestParam(required = false) Long writerId,
            @Parameter(
                    description = "필터 - 특정 가게에 대한 리뷰만 조회",
                    example = "3"
            ) @RequestParam(required = false) Long placeId,
            @Parameter(
                    description = """
                            <p>연관된 resource를 포함할지에 대한 여부
                            <p>연관된 resource(장소, 작성자)가 필요하다면 필요한 resource를 설정하여 요청해야 합니다.
                            <p>성능 최적화와 관련된 부분이니, 반드시 필요한 경우에만 관련 resource를 포함하여 응답하도록 설정해주세요.
                            <p>ex. 리뷰와 작성자 정보도 함께 필요한 경우: <code>/api/reviews?embed=WRITER</code>
                            <p>ex. 리뷰와 작성자, 장소 정보도 함께 필요한 경우: <code>/api/reviews?embed=WRITER,PLACE</code>
                            """,
                    example = "[\"WRITER\", \"PLACE\"]"
            ) @RequestParam(required = false, defaultValue = "") List<ReviewEmbedOption> embed,
            @Parameter(
                    description = "페이지 번호(0부터 시작)",
                    example = "0"
            ) @RequestParam(required = false, defaultValue = "0") int page,
            @Parameter(
                    description = "한 페이지에 담길 데이터의 최대 개수",
                    example = "15"
            ) @RequestParam(required = false, defaultValue = "10") int size
    ) {
        Slice<ReviewWithPlaceMarkedStatusDto> reviewDtos = reviewQueryService.findDtos(userPrincipal.getMemberId(), writerId, placeId, embed, PageRequest.of(page, size));
        return new SliceResponse<FindReviewsResponse>().from(reviewDtos.map(FindReviewsResponse::from));
    }

    @Operation(
            summary = "리뷰 피드 조회",
            description = """
                    <p><strong>Latest version: v1.1</strong>
                    <p>리뷰 피드를 조회합니다.
                    <p>내가 작성한 리뷰는 노출되지 않습니다.
                    <p>정렬 기준은 다음과 같습니다.
                    <ol>
                        <li>리뷰를 작성한 장소의 카테고리가 내가 선호하는 음식 카테고리에 해당되는 경우</li>
                        <li>최근 등록된 순서</li>
                    </ol>
                    """,
            security = @SecurityRequirement(name = "access-token")
    )
    @GetMapping(value = "/v1/reviews/feed", headers = API_MINOR_VERSION_HEADER_NAME + "=1")
    public SliceResponse<FindReviewFeedResponse> findReviewFeedV1_1(
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
        Slice<ReviewWithPlaceMarkedStatusDto> reviewDtos = reviewQueryService.findReviewReed(userPrincipal.getMemberId(), PageRequest.of(page, size));
        return new SliceResponse<FindReviewFeedResponse>().from(reviewDtos.map(FindReviewFeedResponse::from));
    }

    @Operation(
            summary = "내가 작성한 리뷰 조회",
            description = "<p><strong>Latest version: v1.1</strong>" +
                          "<p>내가 작성한 리뷰를 조회합니다." +
                          "<p>정렬 기준은 최근 등록된 순(최신순)입니다.",
            security = @SecurityRequirement(name = "access-token")
    )
    @GetMapping(value = "/v1/reviews/me", headers = API_MINOR_VERSION_HEADER_NAME + "=1")
    public SliceResponse<FindReviewsWrittenByMeResponse> findReviewsWrittenByMeV1_1(
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
        long loginMemberId = userPrincipal.getMemberId();
        Slice<ReviewWithPlaceMarkedStatusDto> reviewDtos = reviewQueryService.findDtos(loginMemberId, loginMemberId, null, List.of(PLACE), PageRequest.of(page, size));
        return new SliceResponse<FindReviewsWrittenByMeResponse>().from(reviewDtos.map(FindReviewsWrittenByMeResponse::from));
    }

    @Operation(
            summary = "리뷰 내용 자동 생성",
            description = "<p><strong>Latest version: v1.1</strong>" +
                          "<p>사용자로부터 장소에 대한 키워드, 메뉴에 대한 키워드들을 받아 리뷰 내용을 자동으로 작성합니다.",
            security = @SecurityRequirement(name = "access-token")
    )
    @GetMapping(value = "/v1/reviews/contents/auto-creations", headers = API_MINOR_VERSION_HEADER_NAME + "=1")
    public GettingAutoCreatedReviewContentResponse getAutoCreatedContentWithGptV1_1(
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

        String result = openAIService.getAutoCreatedReviewContent(placeKeywords, menus, parsedMenuKeywords);
        return new GettingAutoCreatedReviewContentResponse(result);
    }

    @Operation(
            summary = "리뷰 내용 수정",
            description = "<p><strong>Latest version: v1.1</strong>" +
                          "<p>리뷰 내용을 수정합니다.",
            security = @SecurityRequirement(name = "access-token")
    )
    @ApiResponses({
            @ApiResponse(description = "OK", responseCode = "200"),
            @ApiResponse(description = "[3503] 리뷰 수정 권한이 없는 경우.", responseCode = "403", content = @Content)
    })
    @PatchMapping(value = "/v1/reviews/{reviewId}", headers = API_MINOR_VERSION_HEADER_NAME + "=1")
    public ReviewResponse updateV1_1(
            @AuthenticationPrincipal UserPrincipal userPrincipal,
            @Parameter(
                    description = "수정할 리뷰의 PK",
                    example = "1"
            ) @PathVariable Long reviewId,
            @Valid @RequestBody ReviewUpdateRequest updateRequest
    ) {
        return ReviewResponse.from(reviewCommandService.update(userPrincipal.getMemberId(), reviewId, updateRequest.getContent()));
    }

    @Operation(
            summary = "리뷰 삭제",
            description = "<p><strong>Latest version: v1.1</strong>" +
                          "<p>리뷰를 삭제합니다.",
            security = @SecurityRequirement(name = "access-token")
    )
    @ApiResponses({
            @ApiResponse(description = "OK", responseCode = "200"),
            @ApiResponse(description = "[3502] 리뷰 삭제 권한이 없는 경우", responseCode = "403", content = @Content)
    })
    @DeleteMapping(value = "/v1/reviews/{reviewId}", headers = API_MINOR_VERSION_HEADER_NAME + "=1")
    public void deleteV1_1(
            @AuthenticationPrincipal UserPrincipal userPrincipal,
            @Parameter(
                    description = "삭제할 리뷰의 PK",
                    example = "1"
            ) @PathVariable Long reviewId
    ) {
        reviewCommandService.delete(userPrincipal.getMemberId(), reviewId);
    }
}
