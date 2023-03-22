package com.zelusik.eatery.app.controller;

import com.zelusik.eatery.app.config.SecurityConfig;
import com.zelusik.eatery.app.dto.member.request.FavoriteFoodCategoriesUpdateRequest;
import com.zelusik.eatery.app.dto.member.request.TermsAgreeRequest;
import com.zelusik.eatery.app.dto.terms_info.TermsInfoDto;
import com.zelusik.eatery.app.service.MemberService;
import com.zelusik.eatery.global.security.JwtAuthenticationFilter;
import com.zelusik.eatery.global.security.UserPrincipal;
import com.zelusik.eatery.util.MemberTestUtils;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.data.jpa.mapping.JpaMetamodelMappingContext;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.testcontainers.shaded.com.fasterxml.jackson.databind.ObjectMapper;

import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.given;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@DisplayName("[Controller] Member")
@MockBean(JpaMetamodelMappingContext.class)
@WebMvcTest(
        controllers = MemberController.class,
        excludeFilters = @ComponentScan.Filter(
                type = FilterType.ASSIGNABLE_TYPE,
                classes = {SecurityConfig.class, JwtAuthenticationFilter.class}
        )
)
class MemberControllerTest {

    @MockBean
    MemberService memberService;

    private final MockMvc mvc;
    private final ObjectMapper mapper;

    public MemberControllerTest(@Autowired MockMvc mvc) {
        this.mvc = mvc;
        this.mapper = new ObjectMapper();
    }

    @DisplayName("약관 동의 정보가 주어지고, 약관에 동의하면, 약관 동의 결과를 반환한다.")
    @Test
    void givenAgreementOfTermsInfo_whenAgreeToTerms_thenReturnTermsInfoResult() throws Exception {
        // given
        TermsAgreeRequest termsAgreeRequest = TermsAgreeRequest.of(true, true, true, true, false);
        LocalDateTime now = LocalDateTime.now();
        given(memberService.agreeToTerms(anyLong(), any(TermsAgreeRequest.class)))
                .willReturn(TermsInfoDto.of(1L,
                        true,
                        true, now,
                        true, now,
                        true, now,
                        false, now,
                        now,
                        now));

        // when & then
        mvc.perform(
                        post("/api/members/terms")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(mapper.writeValueAsString(termsAgreeRequest))
                                .with(csrf())
                                .with(user(UserPrincipal.of(MemberTestUtils.createMemberDtoWithId())))
                )
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.isNotMinor").value(true))
                .andExpect(jsonPath("$.service").value(true))
                .andExpect(jsonPath("$.userInfo").value(true))
                .andExpect(jsonPath("$.locationInfo").value(true))
                .andExpect(jsonPath("$.marketingReception").value(false));
    }

    @DisplayName("필수 이용약관에 동의하지 않은 약관 동의 정보가 주어지고, 약관에 동의하면, 에러가 발생한다.")
    @Test
    void givenAgreementOfTermsInfoThatNotAgreedToRequiredTerms_whenAgreeToTerms_thenThrowException() throws Exception {
        // given
        TermsAgreeRequest termsAgreeRequest = TermsAgreeRequest.of(true, false, true, true, false);
        LocalDateTime now = LocalDateTime.now();
        given(memberService.agreeToTerms(anyLong(), any(TermsAgreeRequest.class)))
                .willReturn(TermsInfoDto.of(1L,
                        true,
                        false, now,
                        true, now,
                        true, now,
                        false, now,
                        now,
                        now));

        // when & then
        mvc.perform(
                        post("/api/members/terms")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(mapper.writeValueAsString(termsAgreeRequest))
                                .with(csrf())
                                .with(user(UserPrincipal.of(MemberTestUtils.createMemberDtoWithId())))
                )
                .andExpect(status().isUnprocessableEntity())
                .andExpect(jsonPath("$.code").value(1200));
    }

    @DisplayName("내 정보를 조회하면, 회원 정보를 응답한다.")
    @Test
    void given_whenGetMyInfo_thenReturnMemberInfo() throws Exception {
        // given
        long memberId = 1L;
        given(memberService.findDtoById(memberId))
                .willReturn(MemberTestUtils.createMemberDtoWithId(memberId));

        // when & then
        mvc.perform(
                        get("/api/members/me")
                                .with(csrf())
                                .with(user(UserPrincipal.of(MemberTestUtils.createMemberDtoWithId())))
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(memberId));
    }

    @DisplayName("선호 음식 카테고리 목록이 주어지고, 이를 업데이트하면, 수정된 멤버 정보가 반환된다.")
    @Test
    void givenFavoriteFoodCategories_whenUpdatingFavoriteFoodCategories_thenReturnUpdatedMember() throws Exception {
        // given
        FavoriteFoodCategoriesUpdateRequest request = FavoriteFoodCategoriesUpdateRequest.of(List.of("한식", "양식"));
        given(memberService.updateFavoriteFoodCategories(any(), any()))
                .willReturn(MemberTestUtils.createMemberDtoWithId());

        // when & then
        mvc.perform(
                        patch("/api/members/favorite-food")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(mapper.writeValueAsString(request))
                                .with(csrf())
                                .with(user(UserPrincipal.of(MemberTestUtils.createMemberDtoWithId())))
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.nickname").exists())
                .andExpect(jsonPath("$.favoriteFoodCategories").exists());
    }
}