package com.zelusik.eatery.app.controller;

import com.zelusik.eatery.app.dto.review.request.ReviewCreateRequest;
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
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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
            @ApiResponse(description = "1001: 전달받은 파일을 읽을 수 없는 경우.", responseCode = "400", content = @Content),
            @ApiResponse(description = "1350: 장소에 대한 추가 정보를 스크래핑 할 Flask 서버에서 에러가 발생한 경우.", responseCode = "500", content = @Content),
            @ApiResponse(description = "3000: 상세 페이지에서 읽어온 가게 영업시간이 처리할 수 없는 형태일 경우.", responseCode = "500", content = @Content)
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
}
