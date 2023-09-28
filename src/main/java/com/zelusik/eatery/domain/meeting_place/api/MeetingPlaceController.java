package com.zelusik.eatery.domain.meeting_place.api;

import com.zelusik.eatery.global.common.dto.response.SliceResponse;
import com.zelusik.eatery.domain.meeting_place.dto.response.MeetingPlaceResponse;
import com.zelusik.eatery.global.kakao.service.KakaoService;
import com.zelusik.eatery.domain.location.service.LocationQueryService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.SliceImpl;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.constraints.NotBlank;
import java.util.List;
import java.util.stream.Stream;

import static com.zelusik.eatery.global.common.constant.EateryConstants.API_MINOR_VERSION_HEADER_NAME;

@Tag(name = "약속 장소 관련 API")
@RequiredArgsConstructor
@Validated
@RequestMapping("/api")
@RestController
public class MeetingPlaceController {

    public static final int PAGE_SIZE_OF_SEARCHING_MEETING_PLACES = 15;

    private final LocationQueryService locationQueryService;
    private final KakaoService kakaoService;

    @Operation(
            summary = "키워드로 약속 장소 검색하기",
            description = "<p><strong>Latest version: v1.1</strong>" +
                          "<p>키워드로 매칭된 장소 검색 결과를 제공합니다." +
                          "<p>검색 대상은 행정구역(시/도, 시/군/구, 읍/면/동/구), 지하철역, 관광명소와 학교입니다." +
                          "<p>한 페이지에 제공되는 장소의 개수는 30개 미만입니다.",
            security = @SecurityRequirement(name = "access-token")
    )
    @GetMapping(value = "/v1/meeting-places", headers = API_MINOR_VERSION_HEADER_NAME + "=1")
    public SliceResponse<MeetingPlaceResponse> searchMeetingPlacesV1_1(
            @Parameter(
                    description = "검색 키워드",
                    example = "광교"
            ) @RequestParam @NotBlank String keyword,
            @Parameter(
                    description = "페이지 번호(0부터 시작합니다).",
                    example = "0"
            ) @RequestParam(required = false, defaultValue = "0") int page
    ) {
        Page<MeetingPlaceResponse> locations =
                locationQueryService.searchDtosByKeyword(keyword, PageRequest.of(page, PAGE_SIZE_OF_SEARCHING_MEETING_PLACES))
                        .map(MeetingPlaceResponse::from);
        if (locations.getNumberOfElements() >= PAGE_SIZE_OF_SEARCHING_MEETING_PLACES) {
            return new SliceResponse<MeetingPlaceResponse>().from(locations);
        }

        int pageForKakaoSearching;
        if (locations.hasContent()) {
            pageForKakaoSearching = locations.getTotalPages() - locations.getNumber() - 1;
        } else {
            pageForKakaoSearching = page;
        }
        Slice<MeetingPlaceResponse> kakaoPlaces =
                kakaoService.searchKakaoPlacesByKeyword(keyword, PageRequest.of(pageForKakaoSearching, PAGE_SIZE_OF_SEARCHING_MEETING_PLACES))
                        .map(MeetingPlaceResponse::from);

        List<MeetingPlaceResponse> content = Stream.concat(
                locations.getContent().stream(),
                kakaoPlaces.getContent().stream()
        ).toList();

        Slice<MeetingPlaceResponse> res = new SliceImpl<>(
                content,
                PageRequest.of(page, locations.getNumberOfElements() + kakaoPlaces.getNumberOfElements()),
                kakaoPlaces.hasNext()
        );
        return new SliceResponse<MeetingPlaceResponse>().from(res);
    }
}
