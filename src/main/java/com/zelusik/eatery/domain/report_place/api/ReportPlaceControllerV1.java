package com.zelusik.eatery.domain.report_place.api;

import com.zelusik.eatery.domain.report_place.dto.ReportPlaceDto;
import com.zelusik.eatery.domain.report_place.dto.request.ReportPlaceRequest;
import com.zelusik.eatery.domain.report_place.dto.response.PostReportPlaceResponse;
import com.zelusik.eatery.domain.report_place.service.ReportPlaceCommandService;
import com.zelusik.eatery.domain.report_place.service.ReportPlaceQueryService;
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

@Tag(name = "장소 신고 관련 API")
@RequiredArgsConstructor
@RequestMapping("/api/v1/reports/places")
@RestController
public class ReportPlaceControllerV1 {

    private final ReportPlaceCommandService reportPlaceCommandService;
    private final ReportPlaceQueryService reportPlaceQueryService;

    @Operation(summary = "장소 신고(정보 수정 제안)",
            description = "<p><strong>Latest version: v1.1</strong>" +
                    "<p>특정 장소를 신고합니다.</p>" +
                    "<ul><li><code>placeId</code> : 신고하는 장소 id</li>" +
                    "<li><code>reasonOption</code> : 신고하는 이유 선택(POSITION, TIME, CLOSED_DAYS, NUMBER, SNS, ETC 중 택 1) </li>" +
                    "<li><code>reasonDetail</code> : 신고하는 상세 이유 </li></ul>",
            security = @SecurityRequirement(name = "access-token")
    )
    @PostMapping(headers = API_MINOR_VERSION_HEADER_NAME + "=1")
    public ResponseEntity<PostReportPlaceResponse> reportPlaceV1(
            @AuthenticationPrincipal UserPrincipal userPrincipal,
            @RequestBody @Valid ReportPlaceRequest reportPlaceRequest) {
        ReportPlaceDto reportPlaceDto = reportPlaceCommandService.reportPlace(userPrincipal.getMemberId(), reportPlaceRequest);
        return ResponseEntity
                .created(URI.create("/api/v1/reports/places/" + reportPlaceDto.getId()))
                .body(PostReportPlaceResponse.from(reportPlaceDto));
    }

    @Operation(summary = "장소 신고 내역(정보 수정 제안) 단건 조회",
            description = "<p><strong>Latest version: v1.1</strong>" +
                    "<p>전달받은 <code>reportPlaceId</code>에 해당하는 장소 신고 내역을 조회합니다.",
            security = @SecurityRequirement(name = "access-token")
    )
    @GetMapping(value = "/{reportPlaceId}", headers = API_MINOR_VERSION_HEADER_NAME + "=1")
    public PostReportPlaceResponse findReportPlaceByIdV1(
            @Parameter(
                    description = "PK of reportPlace",
                    example = "3"
            ) @PathVariable Long reportPlaceId
    ) {
        ReportPlaceDto reportPlaceDto = reportPlaceQueryService.getDtoByReportPlaceId(reportPlaceId);
        return PostReportPlaceResponse.from(reportPlaceDto);
    }
}