package com.zelusik.eatery.domain.report_review.api;

import com.zelusik.eatery.domain.report_review.dto.ReportReviewDto;
import com.zelusik.eatery.domain.report_review.dto.request.ReportReviewRequest;
import com.zelusik.eatery.domain.report_review.dto.response.PostReportReviewResponse;
import com.zelusik.eatery.domain.report_review.service.ReportReviewCommandService;
import com.zelusik.eatery.domain.report_review.service.ReportReviewQueryService;
import com.zelusik.eatery.global.auth.UserPrincipal;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.net.URI;

import static com.zelusik.eatery.global.common.constant.EateryConstants.API_MINOR_VERSION_HEADER_NAME;

@Tag(name = "리뷰 신고 관련 API")
@RequiredArgsConstructor
@RequestMapping("/api/v1/reports/reviews")
@RestController
public class ReportReviewControllerV1 {

    private final ReportReviewCommandService reportReviewCommandService;
    private final ReportReviewQueryService reportReviewQueryService;

    @Operation(summary = "리뷰 신고",
            description = "<p><strong>Latest version: v1.1</strong>" +
                    "<p>특정 리뷰를 신고합니다.</p>" +
                    "<ul><li><code>reviewId</code> : 신고하는 리뷰 id</li>" +
                    "<li><code>reasonOption</code> : 신고하는 이유 선택(UNRELATED, ADVERTISING, SENSATIONAL, UNAUTHORIZED, PRIVACY, ETC 중 택 1) </li>" +
                    "<li><code>reasonDetail</code> : 신고하는 상세 이유 </li></ul>",
            security = @SecurityRequirement(name = "access-token")
    )
    @PostMapping(headers = API_MINOR_VERSION_HEADER_NAME + "=1")
    public ResponseEntity<PostReportReviewResponse> reportReviewV1(
            @AuthenticationPrincipal UserPrincipal userPrincipal,
            @RequestBody @Valid ReportReviewRequest reportReviewRequest) {
        ReportReviewDto reportReviewDto = reportReviewCommandService.reportReview(userPrincipal.getMemberId(), reportReviewRequest.getReviewId(), reportReviewRequest.getReasonOption(), reportReviewRequest.getReasonDetail());
        return ResponseEntity.created(URI.create("/api/v1/reports/reviews/" + reportReviewDto.getId())).body(PostReportReviewResponse.from(reportReviewDto));
    }

    @Operation(summary = "리뷰 신고 내역 단건 조회",
            description = "<p><strong>Latest version: v1.1</strong>" +
                    "<p>전달받은 <code>reportReviewId</code>에 해당하는 리뷰 신고 내역을 조회합니다.",
            security = @SecurityRequirement(name = "access-token")
    )
    @GetMapping(value = "/{reportReviewId}", headers = API_MINOR_VERSION_HEADER_NAME + "=1")
    public PostReportReviewResponse findReportReviewByIdV1(
            @Parameter(
                    description = "PK of reportReview",
                    example = "3"
            ) @PathVariable Long reportReviewId
    ) {
        ReportReviewDto reportReviewDto = reportReviewQueryService.findDtoByReportReviewId(reportReviewId);
        return PostReportReviewResponse.from(reportReviewDto);
    }
}