package com.zelusik.eatery.unit.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.zelusik.eatery.config.TestSecurityConfig;
import com.zelusik.eatery.constant.member.RoleType;
import com.zelusik.eatery.controller.CurationController;
import com.zelusik.eatery.dto.curation.CurationDto;
import com.zelusik.eatery.dto.curation.CurationElemDto;
import com.zelusik.eatery.dto.curation.CurationElemFileDto;
import com.zelusik.eatery.dto.curation.request.CurationCreateRequest;
import com.zelusik.eatery.dto.curation.request.CurationElemCreateRequest;
import com.zelusik.eatery.dto.curation.response.CurationListResponse;
import com.zelusik.eatery.dto.place.PlaceDto;
import com.zelusik.eatery.dto.place.request.PlaceCreateRequest;
import com.zelusik.eatery.security.UserPrincipal;
import com.zelusik.eatery.service.CurationService;
import com.zelusik.eatery.util.MemberTestUtils;
import com.zelusik.eatery.util.MultipartFileTestUtils;
import com.zelusik.eatery.util.PlaceTestUtils;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;

import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@DisplayName("[Unit] Curation Controller")
@Import(TestSecurityConfig.class)
@WebMvcTest(controllers = CurationController.class)
class CurationControllerTest {

    @MockBean
    private CurationService curationService;

    private final MockMvc mvc;
    private final ObjectMapper mapper;

    @Autowired
    public CurationControllerTest(MockMvc mvc, ObjectMapper mapper) {
        this.mvc = mvc;
        this.mapper = mapper;
    }

    @DisplayName("큐레이션 제목과 함께, 큐레이션을 생성한다.")
    @Test
    void givenCurationTitle_whenCreateCuration_thenReturnCreatedCuration() throws Exception {
        // given
        long curationId = 2L;
        String title = "test curation";
        CurationCreateRequest request = CurationCreateRequest.of(title);
        CurationDto expectedCreatedCuration = createCurationDto(curationId, title);
        given(curationService.create(title)).willReturn(expectedCreatedCuration);

        // when & then
        mvc.perform(
                        post("/api/curation")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(mapper.writeValueAsString(request))
                                .with(user(createTestUserDetails(1L, Set.of(RoleType.USER, RoleType.MANAGER))))
                )
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(curationId))
                .andExpect(jsonPath("$.title").value(title))
                .andExpect(jsonPath("$.curationElems").isArray());
    }

    @DisplayName("일반 유저가 큐레이션을 생성하려고 하면, 접근이 거부된다.")
    @Test
    void givenCurationTitle_whenCreateCurationWithNormalUser_thenThrowAccessDeniedException() throws Exception {
        // given
        String title = "test curation";
        CurationCreateRequest request = CurationCreateRequest.of(title);

        // when & then
        mvc.perform(
                        post("/api/curation")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(mapper.writeValueAsString(request))
                                .with(user(UserPrincipal.of(MemberTestUtils.createMemberDto(1L, Set.of(RoleType.USER)))))
                )
                .andExpect(status().isForbidden());
    }

    @DisplayName("큐레이션의 새로운 콘텐츠가 추가하고자 할 큐레이션 id와 함께 주어지고, 큐레이션에 콘텐츠를 추가하면, 콘텐츠가 추가된 큐레이션 정보가 반환된다.")
    @Test
    void givenNewCurationContentWithCurationId_whenAddContentToCuration_thenReturnUpdatedCuration() throws Exception {
        // given
        long memberId = 1L;
        long curationId = 2L;
        long placeId = 3L;
        PlaceCreateRequest placeRequest = PlaceTestUtils.createPlaceRequest();
        MockMultipartFile mockMultipartFile = MultipartFileTestUtils.createMockMultipartFile();
        CurationDto expectedResult = createCurationDto(
                curationId,
                "test curation",
                List.of(createCurationElemDto(4L, curationId, PlaceTestUtils.createPlaceDto(placeId)))
        );
        given(curationService.addCurationElem(eq(memberId), eq(curationId), any(CurationElemCreateRequest.class)))
                .willReturn(expectedResult);

        // when & then
        mvc.perform(
                        multipart("/api/curation/" + curationId)
                                .file(mockMultipartFile)
                                .param("place.kakaoPid", placeRequest.getKakaoPid())
                                .param("place.name", placeRequest.getName())
                                .param("place.pageUrl", placeRequest.getPageUrl())
                                .param("place.categoryGroupCode", placeRequest.getCategoryGroupCode().toString())
                                .param("place.categoryName", placeRequest.getCategoryName())
                                .param("place.phone", placeRequest.getPhone())
                                .param("place.lotNumberAddress", placeRequest.getLotNumberAddress())
                                .param("place.roadAddress", placeRequest.getRoadAddress())
                                .param("place.lat", placeRequest.getLat())
                                .param("place.lng", placeRequest.getLng())
                                .with(user(createTestUserDetails(memberId, Set.of(RoleType.USER, RoleType.MANAGER))))
                )
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(curationId))
                .andExpect(jsonPath("$.curationElems").isArray())
                .andExpect(jsonPath("$.curationElems.size()").value(1));
    }

    @DisplayName("일반 유저가 큐레이션에 콘텐츠를 추가하려고 하면, 접근이 거부된다.")
    @Test
    void givenNewCurationContentWithCurationId_whenAddContentToCurationWithNormalUser_thenThrowAccessDeniedException() throws Exception {
        // given
        long memberId = 1L;
        long curationId = 2L;
        MockMultipartFile mockMultipartFile = MultipartFileTestUtils.createMockMultipartFile();
        PlaceCreateRequest placeRequest = PlaceTestUtils.createPlaceRequest();

        // when & then
        mvc.perform(
                        multipart("/api/curation/" + curationId)
                                .file(mockMultipartFile)
                                .param("place.kakaoPid", placeRequest.getKakaoPid())
                                .param("place.name", placeRequest.getName())
                                .param("place.pageUrl", placeRequest.getPageUrl())
                                .param("place.categoryGroupCode", placeRequest.getCategoryGroupCode().toString())
                                .param("place.categoryName", placeRequest.getCategoryName())
                                .param("place.phone", placeRequest.getPhone())
                                .param("place.lotNumberAddress", placeRequest.getLotNumberAddress())
                                .param("place.roadAddress", placeRequest.getRoadAddress())
                                .param("place.lat", placeRequest.getLat())
                                .param("place.lng", placeRequest.getLng())
                                .with(user(createTestUserDetails(memberId, Set.of(RoleType.USER))))
                )
                .andExpect(status().isForbidden());
    }

    @DisplayName("큐레이션 id로 큐레이션 정보를 조회하면, 조회된 큐레이션 정보가 반환된다.")
    @Test
    void givenCurationId_whenFindingCuration_thenReturnCuration() throws Exception {
        // given
        long memberId = 1L;
        long curationId = 2L;
        CurationDto expectedCuration = createCurationDto(curationId, "test curation", List.of());
        given(curationService.findDtoById(curationId)).willReturn(expectedCuration);

        // when & then
        mvc.perform(
                        get("/api/curation/" + curationId)
                                .with(user(createTestUserDetails(memberId, Set.of(RoleType.USER))))
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(curationId));
    }

    @DisplayName("큐레이션 목록을 조회하면, 조회된 결과가 반환된다.")
    @Test
    void given_when_then() throws Exception {
        // given
        CurationListResponse expectedResult = CurationListResponse.of(List.of());
        given(curationService.getCurationList()).willReturn(expectedResult);

        // when & then
        mvc.perform(
                        get("/api/curation")
                                .with(user(createTestUserDetails(1L, Set.of(RoleType.USER))))
                )
                .andExpect(status().isOk());
    }

    @NotNull
    private static UserPrincipal createTestUserDetails(long memberId, Set<RoleType> roleTypes) {
        return UserPrincipal.of(MemberTestUtils.createMemberDto(memberId, roleTypes));
    }

    @NotNull
    private static CurationDto createCurationDto(Long id, String title) {
        return CurationDto.of(
                id,
                title,
                List.of(),
                LocalDateTime.of(2023, 1, 1, 0, 0),
                LocalDateTime.of(2023, 1, 1, 0, 0)
        );
    }

    @NotNull
    private static CurationDto createCurationDto(Long id, String title, List<CurationElemDto> curationElems) {
        return CurationDto.of(
                id,
                title,
                curationElems,
                LocalDateTime.of(2023, 1, 1, 0, 0),
                LocalDateTime.of(2023, 1, 1, 0, 0)
        );
    }

    @NotNull
    private static CurationElemDto createCurationElemDto(Long newContentId, Long curationId, PlaceDto placeDto) {
        return CurationElemDto.of(
                newContentId,
                curationId,
                placeDto,
                createCurationElemFileDto(),
                LocalDateTime.of(2023, 1, 1, 0, 0),
                LocalDateTime.of(2023, 1, 1, 0, 0)
        );
    }

    @NotNull
    private static CurationElemFileDto createCurationElemFileDto() {
        return CurationElemFileDto.of(
                100L,
                "original image name",
                "image name stored to s3 bucket",
                "image url",
                "thumbnail image stored name",
                "thumbnail image url",
                LocalDateTime.of(2023, 1, 1, 0, 0),
                LocalDateTime.of(2023, 1, 1, 0, 0)
        );
    }
}