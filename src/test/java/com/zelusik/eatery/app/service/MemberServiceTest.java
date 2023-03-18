package com.zelusik.eatery.app.service;

import com.zelusik.eatery.app.constant.FoodCategory;
import com.zelusik.eatery.app.domain.Member;
import com.zelusik.eatery.app.domain.TermsInfo;
import com.zelusik.eatery.app.dto.member.MemberDto;
import com.zelusik.eatery.app.dto.member.request.TermsAgreeRequest;
import com.zelusik.eatery.app.dto.terms_info.TermsInfoDto;
import com.zelusik.eatery.app.repository.MemberRepository;
import com.zelusik.eatery.app.repository.TermsInfoRepository;
import com.zelusik.eatery.global.exception.member.MemberIdNotFoundException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static com.zelusik.eatery.app.constant.FoodCategory.*;
import static com.zelusik.eatery.util.MemberTestUtils.*;
import static com.zelusik.eatery.util.TermsInfoTestUtils.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowable;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;

@DisplayName("[Service] Member")
@ExtendWith(MockitoExtension.class)
class MemberServiceTest {

    @InjectMocks
    private MemberService sut;

    @Mock
    private MemberRepository memberRepository;
    @Mock
    private TermsInfoRepository termsInfoRepository;

    @DisplayName("회원 정보가 주어지면 회원가입을 진행한 후 등록된 회원 정보를 return한다.")
    @Test
    void givenMemberInfo_whenSignUp_thenSaveAndReturnMember() {
        // given
        MemberDto memberInfo = createMemberDto();
        Member expectedSavedMember = createMember(1L);
        given(memberRepository.save(any(Member.class))).willReturn(expectedSavedMember);

        // when
        MemberDto actualSavedMember = sut.save(memberInfo);

        // then
        then(memberRepository).should().save(any(Member.class));
        assertThat(actualSavedMember.id()).isNotNull();
        assertThat(actualSavedMember.socialUid()).isEqualTo(expectedSavedMember.getSocialUid());
        assertThat(actualSavedMember.loginType()).isEqualTo(expectedSavedMember.getLoginType());
        assertThat(actualSavedMember.email()).isEqualTo(expectedSavedMember.getEmail());
        assertThat(actualSavedMember.nickname()).isEqualTo(expectedSavedMember.getNickname());
        assertThat(actualSavedMember.ageRange()).isEqualTo(expectedSavedMember.getAgeRange());
        assertThat(actualSavedMember.gender()).isEqualTo(expectedSavedMember.getGender());
    }

    @DisplayName("약관 동의 정보가 주어지고, 약관에 동의하면, 약관 동의 결과를 반환한다.")
    @Test
    void givenAgreeOfTermsInfo_whenAgreeToTerms_thenReturnTermsInfoResult() {
        // given
        long memberId = 1L;
        TermsAgreeRequest termsAgreeRequest = TermsAgreeRequest.of(false, true, true, true, true);
        given(termsInfoRepository.save(any(TermsInfo.class))).willReturn(createTermsInfo());
        given(memberRepository.findById(memberId)).willReturn(Optional.of(createMember(memberId)));

        // when
        TermsInfoDto actualResult = sut.agreeToTerms(memberId, termsAgreeRequest);

        // then
        then(termsInfoRepository).should().save(any(TermsInfo.class));
        then(memberRepository).should().findById(memberId);
        assertThat(actualResult.isMinor()).isFalse();
    }

    @DisplayName("회원의 id(PK)가 주어지면 해당하는 회원을 조회한 후 반환한다.")
    @Test
    void givenMemberId_whenFindMember_thenReturnMember() {
        // given
        Long memberId = 1L;
        Member expected = createMember(memberId);
        given(memberRepository.findById(memberId)).willReturn(Optional.of(expected));

        // when
        MemberDto actual = sut.findDtoById(memberId);

        // then
        then(memberRepository).should().findById(memberId);
        assertThat(actual.id()).isEqualTo(expected.getId());
    }

    @DisplayName("존재하지 않는 회원 id(PK)가 주어지고 회원을 조회하면 예외가 발생한다.")
    @Test
    void givenNonExistentMemberId_whenFindMember_thenThrowException() {
        // given
        Long memberId = 10L;
        given(memberRepository.findById(memberId)).willReturn(Optional.empty());

        // when
        Throwable throwable = catchThrowable(() -> sut.findDtoById(memberId));

        // then
        then(memberRepository).should().findById(memberId);
        assertThat(throwable).isInstanceOf(MemberIdNotFoundException.class);
    }

    @DisplayName("회원의 social uid가 주어지면 해당하는 회원을 조회한 후 반환한다.")
    @Test
    void givenSocialUid_whenFindMember_thenReturnMember() {
        // given
        String socialUid = "1234567890";
        Member expected = createMember(1L);
        given(memberRepository.findBySocialUid(socialUid)).willReturn(Optional.of(expected));

        // when
        Optional<MemberDto> optionalMember = sut.findOptionalDtoBySocialUid(socialUid);

        // then
        then(memberRepository).should().findBySocialUid(socialUid);
        assertThat(optionalMember.isPresent()).isTrue();
    }

    @DisplayName("존재하지 않는 회원의 social uid가 주어지고 회원을 조회하면 비어있는 Optional을 반환한다.")
    @Test
    void givenNonExistentSocialUid_whenFindMember_thenReturnEmptyOptional() {
        // given
        String socialUid = "1234567890";
        given(memberRepository.findBySocialUid(socialUid)).willReturn(Optional.empty());

        // when
        Optional<MemberDto> optionalMember = sut.findOptionalDtoBySocialUid(socialUid);

        // then
        then(memberRepository).should().findBySocialUid(socialUid);
        assertThat(optionalMember.isPresent()).isFalse();
    }

    @DisplayName("선호 음식 카테고리 목록이 주어지고, 이를 업데이트하면, 선호 음식 카테고리를 수정한다.")
    @Test
    void givenFavoriteFoodCategories_whenUpdatingFavoriteFoodCategories_thenUpdate() {
        // given
        long memberId = 1L;
        Member findMember = createMember(memberId);
        List<FoodCategory> favoriteFoodCategories = List.of(KOREAN, WESTERN, DESERT);
        given(memberRepository.findById(memberId)).willReturn(Optional.of(findMember));

        // when
        MemberDto updatedMemberDto = sut.updateFavoriteFoodCategories(memberId, favoriteFoodCategories);

        // then
        then(memberRepository).should().findById(memberId);
        assertThat(updatedMemberDto.favoriteFoodCategories()).contains(KOREAN, WESTERN, DESERT);
    }
}
