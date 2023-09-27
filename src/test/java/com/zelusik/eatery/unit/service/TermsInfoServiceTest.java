package com.zelusik.eatery.unit.service;

import com.zelusik.eatery.domain.member.constant.Gender;
import com.zelusik.eatery.domain.member.constant.LoginType;
import com.zelusik.eatery.domain.member.constant.RoleType;
import com.zelusik.eatery.domain.member.entity.Member;
import com.zelusik.eatery.domain.terms_info.entity.TermsInfo;
import com.zelusik.eatery.domain.terms_info.dto.request.AgreeToTermsRequest;
import com.zelusik.eatery.domain.terms_info.dto.TermsInfoDto;
import com.zelusik.eatery.domain.member.repository.MemberRepository;
import com.zelusik.eatery.domain.terms_info.repository.TermsInfoRepository;
import com.zelusik.eatery.domain.terms_info.service.TermsInfoService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.*;

@DisplayName("[Unit] Terms Info Service Test")
@ExtendWith(MockitoExtension.class)
class TermsInfoServiceTest {

    @InjectMocks
    private TermsInfoService sut;

    @Mock
    private MemberRepository memberRepository;
    @Mock
    private TermsInfoRepository termsInfoRepository;

    @DisplayName("회원의 약관 정보가 주어지면, 약관 동의 정보를 저장합니다.")
    @Test
    void givenTermsAgreementInfos_whenSaveTermsInfo_thenReturnSavedTermsInfo() {
        // given
        long loginMemberId = 1L;
        AgreeToTermsRequest request = createTermsAgreeRequest();
        Member member = createMember(loginMemberId);
        given(memberRepository.findByIdAndDeletedAtNull(loginMemberId)).willReturn(Optional.of(member));
        TermsInfo expectedSavedTermsInfo = createTermsInfo(
                2L,
                member,
                request.getIsNotMinor(),
                request.getService(),
                request.getUserInfo(),
                request.getLocationInfo(),
                request.getMarketingReception()
        );
        given(termsInfoRepository.save(any(TermsInfo.class))).willReturn(expectedSavedTermsInfo);

        // when
        TermsInfoDto actualSavedTermsInfo = sut.saveTermsInfo(loginMemberId, request);

        // then
        then(memberRepository).should().findByIdAndDeletedAtNull(loginMemberId);
        then(termsInfoRepository).should().save(any(TermsInfo.class));
        verifyEveryMocksShouldHaveNoMoreInteractions();
        assertThat(actualSavedTermsInfo)
                .hasFieldOrPropertyWithValue("memberId", loginMemberId)
                .hasFieldOrPropertyWithValue("isNotMinor", expectedSavedTermsInfo.getIsNotMinor())
                .hasFieldOrPropertyWithValue("service", expectedSavedTermsInfo.getService())
                .hasFieldOrPropertyWithValue("userInfo", expectedSavedTermsInfo.getUserInfo())
                .hasFieldOrPropertyWithValue("locationInfo", expectedSavedTermsInfo.getLocationInfo())
                .hasFieldOrPropertyWithValue("marketingReception", expectedSavedTermsInfo.getMarketingReception())
        ;
    }

    @DisplayName("회원 id가 주어지면, 해당하는 회원의 약관 동의 정보를 삭제한다.")
    @Test
    void givenMemberId_whenDeleteWithMemberId_thenDelete() {
        // given
        long memberId = 1L;
        willDoNothing().given(termsInfoRepository).deleteByMember_Id(memberId);

        // when
        sut.deleteByMemberId(memberId);

        // then
        then(termsInfoRepository).should().deleteByMember_Id(memberId);
        then(termsInfoRepository).shouldHaveNoMoreInteractions();
        then(memberRepository).shouldHaveNoInteractions();
    }

    private void verifyEveryMocksShouldHaveNoMoreInteractions() {
        then(memberRepository).shouldHaveNoMoreInteractions();
        then(termsInfoRepository).shouldHaveNoMoreInteractions();
    }

    private Member createMember(long memberId) {
        return createMember(memberId, Set.of(RoleType.USER));
    }

    private Member createMember(long memberId, Set<RoleType> roleTypes) {
        return Member.of(
                memberId,
                "profile image url",
                "profile thunmbnail image url",
                "social user id" + memberId,
                LoginType.KAKAO,
                roleTypes,
                "email",
                "nickname",
                LocalDate.of(2000, 1, 1),
                20,
                Gender.MALE,
                LocalDateTime.now(),
                LocalDateTime.now(),
                null
        );
    }

    private TermsInfo createTermsInfo(long termsInfoId, Member member, boolean isNotMinor, boolean service, boolean userInfo, boolean locationInfo, boolean marketingReception) {
        LocalDateTime now = LocalDateTime.now();
        return TermsInfo.of(
                termsInfoId,
                member,
                isNotMinor,
                service, now,
                userInfo, now,
                locationInfo, now,
                marketingReception, now,
                now,
                now
        );
    }

    private AgreeToTermsRequest createTermsAgreeRequest() {
        return new AgreeToTermsRequest(
                true, true, true, true, true
        );
    }
}