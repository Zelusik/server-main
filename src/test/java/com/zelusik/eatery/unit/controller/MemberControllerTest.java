package com.zelusik.eatery.unit.controller;

import com.zelusik.eatery.config.TestSecurityConfig;
import com.zelusik.eatery.constant.member.Gender;
import com.zelusik.eatery.constant.review.MemberDeletionSurveyType;
import com.zelusik.eatery.controller.MemberController;
import com.zelusik.eatery.dto.member.MemberDto;
import com.zelusik.eatery.dto.member.request.FavoriteFoodCategoriesUpdateRequest;
import com.zelusik.eatery.dto.member.request.MemberUpdateRequest;
import com.zelusik.eatery.dto.member.request.TermsAgreeRequest;
import com.zelusik.eatery.dto.review.request.MemberDeletionSurveyRequest;
import com.zelusik.eatery.dto.terms_info.TermsInfoDto;
import com.zelusik.eatery.security.UserPrincipal;
import com.zelusik.eatery.service.MemberService;
import com.zelusik.eatery.util.MemberTestUtils;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.SliceImpl;
import org.springframework.data.jpa.mapping.JpaMetamodelMappingContext;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.test.web.servlet.MockMvc;
import org.testcontainers.shaded.com.fasterxml.jackson.databind.ObjectMapper;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import static com.zelusik.eatery.util.MemberTestUtils.createMemberDtoWithId;
import static org.hamcrest.Matchers.hasSize;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.given;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@DisplayName("[Unit] Member Controller")
@MockBean(JpaMetamodelMappingContext.class)
@Import(TestSecurityConfig.class)
@WebMvcTest(controllers = MemberController.class)
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
                                .with(user(createTestUserDetails()))
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
                                .with(user(createTestUserDetails()))
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
                .willReturn(createMemberDtoWithId(memberId));

        // when & then
        mvc.perform(
                        get("/api/members")
                                .with(user(createTestUserDetails()))
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(memberId));
    }

    @DisplayName("주어진 검색 키워드로 회원을 검색하면, 검색된 회원 목록이 반환된다.")
    @Test
    void givenSearchKeyword_whenSearchMembersByKeyword_thenReturnSearchedMembers() throws Exception {
        // given
        String searchKeyword = "test";
        List<MemberDto> expectedResult = List.of(createMemberDtoWithId(2L));
        given(memberService.searchDtosByKeyword(eq(searchKeyword), any(Pageable.class))).willReturn(new SliceImpl<>(expectedResult));

        // when & then
        mvc.perform(
                        get("/api/members/search")
                                .queryParam("keyword", searchKeyword)
                                .with(user(createTestUserDetails()))
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.numOfElements").value(expectedResult.size()))
                .andExpect(jsonPath("$.contents", hasSize(expectedResult.size())));
    }

    @DisplayName("프로필 이미지를 제외한 수정할 회원 정보가 주어지고, 내 정보를 수정한다.")
    @Test
    void givenMemberUpdateInfoWithoutProfileImage_whenUpdatingMyInfo_thenUpdate() throws Exception {
        // given
        long memberId = 1L;
        MemberUpdateRequest memberUpdateInfo = new MemberUpdateRequest("update", LocalDate.of(2020, 1, 1), Gender.ETC, null);
        given(memberService.update(eq(memberId), any(MemberUpdateRequest.class)))
                .willReturn(createMemberDtoWithId(memberId));

        // when & then
        // TODO: 프로필 이미지는 어떻게 넣어야 하는지 확인 후 코드 수정 필요
        mvc.perform(
                        multipart(HttpMethod.PUT, "/api/members")
                                .param("nickname", memberUpdateInfo.getNickname())
                                .param("birthDay", memberUpdateInfo.getBirthDay().toString())
                                .param("gender", memberUpdateInfo.getGender().toString())
                                .with(user(createTestUserDetails()))
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
                .willReturn(createMemberDtoWithId());

        // when & then
        mvc.perform(
                        put("/api/members/favorite-food")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(mapper.writeValueAsString(request))
                                .with(user(createTestUserDetails()))
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.nickname").exists())
                .andExpect(jsonPath("$.favoriteFoodCategories").exists());
    }

    @DisplayName("회원 탈퇴를 하면, 회원과 약관 동의 정보를 삭제하고 탈퇴 설문 정보를 반환한다.")
    @Test
    void given_whenDeleteMember_thenDeleteMemberAndReturnSurvey() throws Exception {
        // given
        long memberId = 1L;
        MemberDeletionSurveyType surveyType = MemberDeletionSurveyType.NOT_TRUST;
        given(memberService.delete(memberId, surveyType))
                .willReturn(MemberTestUtils.createMemberDeletionSurveyDto(memberId, surveyType));

        // when & then
        mvc.perform(
                        delete("/api/members")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(mapper.writeValueAsString(MemberDeletionSurveyRequest.of(surveyType)))
                                .with(user(createTestUserDetails()))
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.memberId").value(memberId))
                .andExpect(jsonPath("$.survey").value(surveyType.getDescription()));
    }

    private UserDetails createTestUserDetails() {
        return UserPrincipal.of(createMemberDtoWithId());
    }
}