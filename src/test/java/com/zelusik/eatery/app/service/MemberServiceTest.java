package com.zelusik.eatery.app.service;

import com.zelusik.eatery.app.constant.FoodCategory;
import com.zelusik.eatery.app.constant.member.Gender;
import com.zelusik.eatery.app.constant.review.MemberDeletionSurveyType;
import com.zelusik.eatery.app.domain.member.Member;
import com.zelusik.eatery.app.domain.member.MemberDeletionSurvey;
import com.zelusik.eatery.app.domain.member.TermsInfo;
import com.zelusik.eatery.app.dto.member.MemberDeletionSurveyDto;
import com.zelusik.eatery.app.dto.member.MemberDto;
import com.zelusik.eatery.app.dto.member.request.MemberUpdateRequest;
import com.zelusik.eatery.app.dto.member.request.TermsAgreeRequest;
import com.zelusik.eatery.app.dto.terms_info.TermsInfoDto;
import com.zelusik.eatery.app.repository.member.MemberDeletionSurveyRepository;
import com.zelusik.eatery.app.repository.member.MemberRepository;
import com.zelusik.eatery.app.repository.member.TermsInfoRepository;
import com.zelusik.eatery.global.exception.member.MemberIdNotFoundException;
import com.zelusik.eatery.util.MemberTestUtils;
import com.zelusik.eatery.util.MultipartFileTestUtils;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static com.zelusik.eatery.app.constant.FoodCategory.*;
import static com.zelusik.eatery.util.MemberTestUtils.*;
import static com.zelusik.eatery.util.TermsInfoTestUtils.createTermsInfo;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowable;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.*;

@DisplayName("[Service] Member")
@ExtendWith(MockitoExtension.class)
class MemberServiceTest {

    @InjectMocks
    private MemberService sut;

    @Mock
    private ProfileImageService profileImageService;
    @Mock
    private MemberRepository memberRepository;
    @Mock
    private TermsInfoRepository termsInfoRepository;
    @Mock
    private MemberDeletionSurveyRepository memberDeletionSurveyRepository;

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
        assertThat(actualSavedMember.getId()).isNotNull();
        assertThat(actualSavedMember.getSocialUid()).isEqualTo(expectedSavedMember.getSocialUid());
        assertThat(actualSavedMember.getLoginType()).isEqualTo(expectedSavedMember.getLoginType());
        assertThat(actualSavedMember.getEmail()).isEqualTo(expectedSavedMember.getEmail());
        assertThat(actualSavedMember.getNickname()).isEqualTo(expectedSavedMember.getNickname());
        assertThat(actualSavedMember.getAgeRange()).isEqualTo(expectedSavedMember.getAgeRange());
        assertThat(actualSavedMember.getGender()).isEqualTo(expectedSavedMember.getGender());
    }

    @DisplayName("약관 동의 정보가 주어지고, 약관에 동의하면, 약관 동의 결과를 반환한다.")
    @Test
    void givenAgreeOfTermsInfo_whenAgreeToTerms_thenReturnTermsInfoResult() {
        // given
        long memberId = 1L;
        TermsAgreeRequest termsAgreeRequest = TermsAgreeRequest.of(true, true, true, true, true);
        given(termsInfoRepository.save(any(TermsInfo.class))).willReturn(createTermsInfo());
        given(memberRepository.findByIdAndDeletedAtNull(memberId)).willReturn(Optional.of(createMember(memberId)));

        // when
        TermsInfoDto actualResult = sut.agreeToTerms(memberId, termsAgreeRequest);

        // then
        then(termsInfoRepository).should().save(any(TermsInfo.class));
        then(memberRepository).should().findByIdAndDeletedAtNull(memberId);
        assertThat(actualResult.getIsNotMinor()).isTrue();
    }

    @DisplayName("회원의 id(PK)가 주어지면 해당하는 회원을 조회한 후 반환한다.")
    @Test
    void givenMemberId_whenFindMember_thenReturnMember() {
        // given
        Long memberId = 1L;
        Member expected = createMember(memberId);
        given(memberRepository.findByIdAndDeletedAtNull(memberId)).willReturn(Optional.of(expected));

        // when
        MemberDto actual = sut.findDtoById(memberId);

        // then
        then(memberRepository).should().findByIdAndDeletedAtNull(memberId);
        assertThat(actual.getId()).isEqualTo(expected.getId());
    }

    @DisplayName("존재하지 않는 회원 id(PK)가 주어지고 회원을 조회하면 예외가 발생한다.")
    @Test
    void givenNonExistentMemberId_whenFindMember_thenThrowException() {
        // given
        Long memberId = 10L;
        given(memberRepository.findByIdAndDeletedAtNull(memberId)).willReturn(Optional.empty());

        // when
        Throwable throwable = catchThrowable(() -> sut.findDtoById(memberId));

        // then
        then(memberRepository).should().findByIdAndDeletedAtNull(memberId);
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
        Optional<MemberDto> optionalMember = sut.findOptionalDtoBySocialUidWithDeleted(socialUid);

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
        Optional<MemberDto> optionalMember = sut.findOptionalDtoBySocialUidWithDeleted(socialUid);

        // then
        then(memberRepository).should().findBySocialUid(socialUid);
        assertThat(optionalMember.isPresent()).isFalse();
    }

    @DisplayName("프로필 이미지를 제외하고 수정할 회원 정보가 주어지고, 회원 정보를 수정하면, 주어진 정보로 회원 정보가 수정된다.")
    @Test
    void givenMemberUpdateInfoWithoutProfileImage_whenUpdatingMemberInfo_thenUpdate() {
        // given
        long memberId = 1L;
        Member findMember = createMember(memberId);
        MemberUpdateRequest memberUpdateInfo = MemberUpdateRequest.of(
                "update",
                LocalDate.of(2020, 1, 1),
                Gender.ETC,
                null
        );
        given(memberRepository.findByIdAndDeletedAtNull(memberId)).willReturn(Optional.of(findMember));

        // when
        MemberDto updatedMemberDto = sut.updateMember(memberId, memberUpdateInfo);

        // then
        then(memberRepository).should().findByIdAndDeletedAtNull(memberId);
        assertThat(updatedMemberDto.getNickname()).isEqualTo(memberUpdateInfo.getNickname());
        assertThat(updatedMemberDto.getBirthDay()).isEqualTo(memberUpdateInfo.getBirthDay());
        assertThat(updatedMemberDto.getGender()).isEqualTo(memberUpdateInfo.getGender());
    }

    @DisplayName("수정할 회원 정보가 주어지고, 회원 정보를 수정하면, 주어진 정보로 회원 정보가 수정된다.")
    @Test
    void givenMemberUpdateInfo_whenUpdatingMemberInfo_thenUpdate() {
        // given
        long memberId = 1L;
        Member findMember = createMember(memberId);
        MemberUpdateRequest memberUpdateInfo = MemberUpdateRequest.of(
                "update",
                LocalDate.of(2020, 1, 1),
                Gender.ETC,
                MultipartFileTestUtils.createMockImageDto()
        );
        given(memberRepository.findByIdAndDeletedAtNull(memberId))
                .willReturn(Optional.of(findMember));
        given(profileImageService.upload(any(Member.class), eq(memberUpdateInfo.getProfileImage())))
                .willReturn(createProfileImage());

        // when
        MemberDto updatedMemberDto = sut.updateMember(memberId, memberUpdateInfo);

        // then
        then(memberRepository).should().findByIdAndDeletedAtNull(memberId);
        then(profileImageService).should().upload(any(Member.class), eq(memberUpdateInfo.getProfileImage()));
        assertThat(updatedMemberDto.getNickname()).isEqualTo(memberUpdateInfo.getNickname());
        assertThat(updatedMemberDto.getBirthDay()).isEqualTo(memberUpdateInfo.getBirthDay());
        assertThat(updatedMemberDto.getGender()).isEqualTo(memberUpdateInfo.getGender());
    }

    @DisplayName("선호 음식 카테고리 목록이 주어지고, 이를 업데이트하면, 선호 음식 카테고리를 수정한다.")
    @Test
    void givenFavoriteFoodCategories_whenUpdatingFavoriteFoodCategories_thenUpdate() {
        // given
        long memberId = 1L;
        Member findMember = createMember(memberId);
        List<FoodCategory> favoriteFoodCategories = List.of(KOREAN, WESTERN, DESERT);
        given(memberRepository.findByIdAndDeletedAtNull(memberId)).willReturn(Optional.of(findMember));

        // when
        MemberDto updatedMemberDto = sut.updateFavoriteFoodCategories(memberId, favoriteFoodCategories);

        // then
        then(memberRepository).should().findByIdAndDeletedAtNull(memberId);
        assertThat(updatedMemberDto.getFavoriteFoodCategories()).contains(KOREAN, WESTERN, DESERT);
    }

    @DisplayName("회원 탈퇴를 하면, 회원과 약관 동의 정보를 삭제하고 탈퇴 설문 정보를 반환한다.")
    @Test
    void given_whenDeleteMember_thenDeleteMemberAndReturnSurvey() {
        // given
        long memberId = 1L;
        MemberDeletionSurveyType surveyType = MemberDeletionSurveyType.NOT_TRUST;
        Member findMember = createMemberWithTermsInfo(memberId);
        TermsInfo memberTermsInfo = findMember.getTermsInfo();
        given(memberRepository.findByIdAndDeletedAtNull(memberId)).willReturn(Optional.of(findMember));
        willDoNothing().given(termsInfoRepository).delete(memberTermsInfo);
        willDoNothing().given(memberRepository).delete(findMember);
        given(memberDeletionSurveyRepository.save(any(MemberDeletionSurvey.class)))
                .willReturn(MemberTestUtils.createMemberDeletionSurvey(findMember, surveyType));

        // when
        MemberDeletionSurveyDto surveyResult = sut.delete(memberId, surveyType);

        // then
        then(memberRepository).should().findByIdAndDeletedAtNull(memberId);
        then(termsInfoRepository).should().delete(memberTermsInfo);
        then(memberRepository).should().delete(findMember);
        then(memberDeletionSurveyRepository).should().save(any(MemberDeletionSurvey.class));
        assertThat(findMember.getTermsInfo()).isNull(); // 회원의 약관동의 정보가 null이 된다.
        assertThat(surveyResult.getSurveyType()).isEqualTo(surveyType);
    }
}
