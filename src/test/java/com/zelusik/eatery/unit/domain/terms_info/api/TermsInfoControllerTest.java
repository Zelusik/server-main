package com.zelusik.eatery.unit.domain.terms_info.api;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.zelusik.eatery.config.TestSecurityConfig;
import com.zelusik.eatery.global.common.constant.EateryConstants;
import com.zelusik.eatery.global.common.constant.FoodCategoryValue;
import com.zelusik.eatery.domain.member.constant.Gender;
import com.zelusik.eatery.domain.member.constant.LoginType;
import com.zelusik.eatery.domain.member.constant.RoleType;
import com.zelusik.eatery.domain.terms_info.api.TermsInfoController;
import com.zelusik.eatery.domain.member.dto.MemberDto;
import com.zelusik.eatery.domain.terms_info.dto.request.AgreeToTermsRequest;
import com.zelusik.eatery.domain.terms_info.dto.TermsInfoDto;
import com.zelusik.eatery.global.security.UserPrincipal;
import com.zelusik.eatery.domain.terms_info.service.TermsInfoService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

import static com.zelusik.eatery.global.common.constant.EateryConstants.API_MINOR_VERSION_HEADER_NAME;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@DisplayName("[Unit] Terms Info Controller Test")
@Import(TestSecurityConfig.class)
@WebMvcTest(controllers = TermsInfoController.class)
class TermsInfoControllerTest {

    @MockBean
    private TermsInfoService termsInfoService;

    private final MockMvc mvc;
    private final ObjectMapper mapper;

    @Autowired
    public TermsInfoControllerTest(TermsInfoService termsInfoService, MockMvc mvc, ObjectMapper mapper) {
        this.termsInfoService = termsInfoService;
        this.mvc = mvc;
        this.mapper = mapper;
    }

    @DisplayName("약관 동의 정보가 주어지고, 약관 동의 정보를 저장하면, 저장된 약관 동의 정보가 반환된다.")
    @Test
    void givenTermsAgreementInfos_whenSaveTermsInfo_thenReturnSavedTermsInfo() throws Exception {
        // given
        long loginMemberId = 1L;
        AgreeToTermsRequest agreeToTermsRequest = new AgreeToTermsRequest(true, true, true, true, true);
        TermsInfoDto expectedResult = createTermsInfoDto(2L, loginMemberId, true, true, true, true, true);
        given(termsInfoService.saveTermsInfo(eq(loginMemberId), any(AgreeToTermsRequest.class))).willReturn(expectedResult);

        // when & then
        mvc.perform(
                        post("/api/v1/members/terms")
                                .header(API_MINOR_VERSION_HEADER_NAME, 1)
                                .content(mapper.writeValueAsString(agreeToTermsRequest))
                                .contentType(MediaType.APPLICATION_JSON)
                                .with(user(createTestUserDetails(loginMemberId)))
                )
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(expectedResult.getId()))
                .andExpect(jsonPath("$.memberId").value(expectedResult.getMemberId()))
                .andExpect(jsonPath("$.isNotMinor").value(expectedResult.getIsNotMinor()))
                .andExpect(jsonPath("$.service").value(expectedResult.getService()))
                .andExpect(jsonPath("$.userInfo").value(expectedResult.getUserInfo()))
                .andExpect(jsonPath("$.locationInfo").value(expectedResult.getLocationInfo()))
                .andExpect(jsonPath("$.marketingReception").value(expectedResult.getMarketingReception()))
                .andDo(print());
    }

    @DisplayName("필수 동의 항목에 동의하지 않은 약관 동의 정보가 주어지고, 약관 동의 정보를 저장하려고 하면, 422 에러가 응답된다.")
    @Test
    void givenTermsAgreementInfosThatHasNotAgreedToTheRequiredAgreementElems_whenSaveTermsInfo_thenResponse422Error() throws Exception {
        // given
        long loginMemberId = 1L;
        AgreeToTermsRequest agreeToTermsRequest = new AgreeToTermsRequest(true, false, true, true, true);

        // when & then
        mvc.perform(
                        post("/api/v1/members/terms")
                                .header(API_MINOR_VERSION_HEADER_NAME, 1)
                                .content(mapper.writeValueAsString(agreeToTermsRequest))
                                .contentType(MediaType.APPLICATION_JSON)
                                .with(user(createTestUserDetails(loginMemberId)))
                )
                .andExpect(status().isUnprocessableEntity())
                .andDo(print());
    }

    private UserDetails createTestUserDetails(long memberId) {
        return UserPrincipal.of(createMemberDto(memberId, Set.of(RoleType.USER)));
    }

    private MemberDto createMemberDto(Long memberId, Set<RoleType> roleTypes) {
        return new MemberDto(
                memberId,
                EateryConstants.defaultProfileImageUrl,
                EateryConstants.defaultProfileThumbnailImageUrl,
                "1234567890",
                LoginType.KAKAO,
                roleTypes,
                "test@test.com",
                "test",
                LocalDate.of(2000, 1, 1),
                20,
                Gender.MALE,
                List.of(FoodCategoryValue.KOREAN),
                null
        );
    }

    private TermsInfoDto createTermsInfoDto(long termsInfoId, long memberId, boolean isNotMinor, boolean service, boolean userInfo, boolean locationInfo, boolean marketingReception) {
        LocalDateTime now = LocalDateTime.now();
        return new TermsInfoDto(
                termsInfoId,
                memberId,
                isNotMinor,
                service, now,
                userInfo, now,
                locationInfo, now,
                marketingReception, now
        );
    }
}