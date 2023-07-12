package com.zelusik.eatery.controller;

import com.zelusik.eatery.dto.curation.CurationDto;
import com.zelusik.eatery.dto.curation.request.CurationCreateRequest;
import com.zelusik.eatery.dto.curation.request.CurationElemCreateRequest;
import com.zelusik.eatery.dto.curation.response.CurationElemResponse;
import com.zelusik.eatery.dto.curation.response.CurationListResponse;
import com.zelusik.eatery.dto.curation.response.CurationResponse;
import com.zelusik.eatery.security.UserPrincipal;
import com.zelusik.eatery.service.CurationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springdoc.api.annotations.ParameterObject;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.net.URI;
import java.util.List;

@Tag(name = "큐레이션 관련 API")
@RequiredArgsConstructor
@RequestMapping("/api/curation")
@RestController
public class CurationController {

    private final CurationService curationService;

    @Operation(
            summary = "큐레이션 생성 (관리자용)",
            description = "<p>큐레이션 제목을 전달받아, 해당하는 큐레이션을 생성합니다." +
                    "<p>큐레이션에 장소 추가는 별도로 진행해야 합니다. (추후 develop 고려 항목)",
            security = @SecurityRequirement(name = "access-token")
    )
    @PostMapping
    public ResponseEntity<CurationResponse> create(
            @Valid @RequestBody CurationCreateRequest request
    ) {
        CurationResponse response = CurationResponse.from(curationService.create(request.getTitle()));

        return ResponseEntity
                .created(URI.create("/api/curation/" + response.getId()))
                .body(response);
    }

    @Operation(
            summary = "큐레이션에 콘텐츠 추가 (관리자용)",
            description = "<p>장소와 이미지를 전달받아 원하는 큐레이션에 콘텐츠를 추가합니다.",
            security = @SecurityRequirement(name = "access-token")
    )
    @PostMapping(value = "/{curationId}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<CurationResponse> addCurationElem(
            @AuthenticationPrincipal UserPrincipal userPrincipal,
            @Parameter(
                    description = "콘텐츠를 추가하고자 하는 큐레이션의 PK",
                    example = "1"
            ) @PathVariable Long curationId,
            @ParameterObject @Valid @ModelAttribute CurationElemCreateRequest request
    ) {
        CurationResponse response = CurationResponse.from(curationService.addCurationElem(userPrincipal.getMemberId(), curationId, request));

        return ResponseEntity
                .created(URI.create("/api/curation/" + curationId)) // TODO: 새로 생성된 큐레이션 콘텐츠에 접근할 수 있는 uri로 변경되어야 함
                .body(response);
    }

    @Operation(
            summary = "큐레이션 단건 조회",
            description = "<p>큐레이션을 단건 조회합니다.",
            security = @SecurityRequirement(name = "access-token")
    )
    @GetMapping("/{curationId}")
    public CurationResponse find(
            @Parameter(
                    description = "조회하고자 하는 큐레이션의 PK",
                    example = "1"
            ) @PathVariable Long curationId
    ) {
        return CurationResponse.from(CurationDto.from(curationService.findEntityById(curationId)));
    }

    @Operation(
            summary = "큐레이션 목록 조회",
            description = "<p>큐레이션 항목들을 불러옵니다."
    )
    @GetMapping
    public CurationListResponse findAll() {
        return curationService.getCurationList();
    }
}
