package com.zelusik.eatery.app.service;

import com.zelusik.eatery.app.domain.Member;
import com.zelusik.eatery.app.dto.member.MemberDto;
import com.zelusik.eatery.app.repository.MemberRepository;
import com.zelusik.eatery.global.exception.member.MemberIdNotFoundException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static com.zelusik.eatery.util.TestMemberUtil.createMemberDto;
import static com.zelusik.eatery.util.TestMemberUtil.createMemberWithId;
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

    @DisplayName("회원 정보가 주어지면 회원가입을 진행한 후 등록된 회원 정보를 return한다.")
    @Test
    void givenMemberInfo_whenSignUp_thenSaveAndReturnMember() {
        // given
        MemberDto memberInfo = createMemberDto();
        Member expectedSavedMember = createMemberWithId();
        given(memberRepository.save(any(Member.class))).willReturn(expectedSavedMember);

        // when
        MemberDto actualSavedMember = sut.signUp(memberInfo);

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

    @DisplayName("회원의 id(PK)가 주어지면 해당하는 회원을 조회한 후 반환한다.")
    @Test
    void givenMemberId_whenFindMember_thenReturnMember() {
        // given
        Long memberId = 1L;
        Member expected = createMemberWithId();
        given(memberRepository.findById(memberId)).willReturn(Optional.of(expected));

        // when
        MemberDto actual = sut.findMemberById(memberId);

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
        Throwable throwable = catchThrowable(() -> sut.findMemberById(memberId));

        // then
        then(memberRepository).should().findById(memberId);
        assertThat(throwable).isInstanceOf(MemberIdNotFoundException.class);
    }

    @DisplayName("회원의 social uid가 주어지면 해당하는 회원을 조회한 후 반환한다.")
    @Test
    void givenSocialUid_whenFindMember_thenReturnMember() {
        // given
        String socialUid = "1234567890";
        Member expected = createMemberWithId();
        given(memberRepository.findBySocialUid(socialUid)).willReturn(Optional.of(expected));

        // when
        Optional<MemberDto> optionalMember = sut.findOptionalMemberBySocialUid(socialUid);

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
        Optional<MemberDto> optionalMember = sut.findOptionalMemberBySocialUid(socialUid);

        // then
        then(memberRepository).should().findBySocialUid(socialUid);
        assertThat(optionalMember.isPresent()).isFalse();
    }
}
