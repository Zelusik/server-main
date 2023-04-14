package com.zelusik.eatery.dto.curation.request;

import com.zelusik.eatery.dto.place.request.PlaceCreateRequest;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;
import org.springframework.web.multipart.MultipartFile;

@AllArgsConstructor(access = AccessLevel.PRIVATE)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Setter
@Getter
public class CurationElemCreateRequest {

    @Schema(description = "장소 정보")
    PlaceCreateRequest place;

    @Schema(description = "장소에 대한 이미지")
    private MultipartFile image;
}
