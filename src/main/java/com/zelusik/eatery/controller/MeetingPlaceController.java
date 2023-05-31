package com.zelusik.eatery.controller;

import com.zelusik.eatery.dto.SliceResponse;
import com.zelusik.eatery.dto.place.response.MeetingPlaceResponse;
import com.zelusik.eatery.service.KakaoService;
import com.zelusik.eatery.service.LocationService;
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

import static com.zelusik.eatery.constant.ConstantUtil.PAGE_SIZE_OF_SEARCHING_MEETING_PLACES;

@Tag(name = "약속 장소 관련 API")
@RequiredArgsConstructor
@Validated
@RequestMapping("/api/meeting-places")
@RestController
public class MeetingPlaceController {

    private final LocationService locationService;
    private final KakaoService kakaoService;

    @Operation(
            summary = "키워드로 약속 장소 검색하기",
            description = "<p>키워드로 매칭된 장소 검색 결과를 제공합니다." +
                    "<p>검색 대상은 행정구역(시/도, 시/군/구, 읍/면/동/구), 지하철역, 관광명소와 학교입니다." +
                    "<p>한 페이지에 제공되는 장소의 개수는 30개 미만입니다.",
            security = @SecurityRequirement(name = "access-token")
    )
    @GetMapping
    public SliceResponse<MeetingPlaceResponse> searchMeetingPlaces(
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
                locationService.searchDtosByKeyword(keyword, PageRequest.of(page, PAGE_SIZE_OF_SEARCHING_MEETING_PLACES))
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