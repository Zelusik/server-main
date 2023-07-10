package com.zelusik.eatery.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.zelusik.eatery.config.TestSecurityConfig;
import com.zelusik.eatery.constant.member.RoleType;
import com.zelusik.eatery.dto.curation.CurationDto;
import com.zelusik.eatery.dto.curation.request.CurationCreateRequest;
import com.zelusik.eatery.security.UserPrincipal;
import com.zelusik.eatery.service.CurationService;
import com.zelusik.eatery.util.MemberTestUtils;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

import static org.mockito.BDDMockito.given;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
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
                                .with(user(UserPrincipal.of(MemberTestUtils.createMemberDtoWithId(1L, Set.of(RoleType.USER, RoleType.MANAGER)))))
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
                                .with(user(UserPrincipal.of(MemberTestUtils.createMemberDtoWithId(1L, Set.of(RoleType.USER)))))
                )
                .andExpect(status().isForbidden());
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
}