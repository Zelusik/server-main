package com.zelusik.eatery.app.controller;

import com.zelusik.eatery.app.dto.curation.request.CurationCreateRequest;
import com.zelusik.eatery.app.dto.curation.response.CurationResponse;
import com.zelusik.eatery.app.service.CurationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springdoc.api.annotations.ParameterObject;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.net.URI;

@Tag(name = "큐레이션 관련 API")
@RequiredArgsConstructor
@RequestMapping("/api/curation")
@RestController
public class CurationController {

    private final CurationService curationService;

    @Operation(
            summary = "큐레이션 생성",
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
}
