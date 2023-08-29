package com.zelusik.eatery.unit.service;

import com.zelusik.eatery.constant.FoodCategoryValue;
import com.zelusik.eatery.constant.member.Gender;
import com.zelusik.eatery.constant.member.RoleType;
import com.zelusik.eatery.constant.review.MemberDeletionSurveyType;
import com.zelusik.eatery.constant.review.ReviewKeywordValue;
import com.zelusik.eatery.domain.member.FavoriteFoodCategory;
import com.zelusik.eatery.domain.member.Member;
import com.zelusik.eatery.domain.member.MemberDeletionSurvey;
import com.zelusik.eatery.domain.member.TermsInfo;
import com.zelusik.eatery.dto.member.MemberDeletionSurveyDto;
import com.zelusik.eatery.dto.member.MemberDto;
import com.zelusik.eatery.dto.member.MemberProfileInfoDto;
import com.zelusik.eatery.dto.member.request.MemberUpdateRequest;
import com.zelusik.eatery.dto.member.request.TermsAgreeRequest;
import com.zelusik.eatery.dto.terms_info.TermsInfoDto;
import com.zelusik.eatery.exception.member.MemberIdNotFoundException;
import com.zelusik.eatery.exception.member.MemberNotFoundException;
import com.zelusik.eatery.repository.member.FavoriteFoodCategoryRepository;
import com.zelusik.eatery.repository.member.MemberDeletionSurveyRepository;
import com.zelusik.eatery.repository.member.MemberRepository;
import com.zelusik.eatery.repository.member.TermsInfoRepository;
import com.zelusik.eatery.service.MemberService;
import com.zelusik.eatery.service.ProfileImageService;
import com.zelusik.eatery.util.MemberTestUtils;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentMatchers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.SliceImpl;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static com.zelusik.eatery.constant.FoodCategoryValue.*;
import static com.zelusik.eatery.util.MemberTestUtils.*;
import static com.zelusik.eatery.util.MultipartFileTestUtils.createMockMultipartFile;
import static com.zelusik.eatery.util.TermsInfoTestUtils.createTermsInfo;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowable;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.*;

@DisplayName("[Unit] Member Service")
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
    @Mock
    private FavoriteFoodCategoryRepository favoriteFoodCategoryRepository;

    @DisplayName("회원 정보가 주어지면 회원가입을 진행한 후 등록된 회원 정보를 return한다.")
    @Test
    void givenMemberInfo_whenSignUp_thenSaveAndReturnMember() {
        // given
        MemberDto memberInfo = createNotSavedMemberDto(Set.of(RoleType.USER));
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

    @DisplayName("주어진 검색 키워드로 회원을 검색하면, 검색된 회원 목록이 반환된다.")
    @Test
    void givenSearchKeyword_whenSearchMembersByKeyword_thenReturnSearchedMembers() {
        // given
        String searchKeyword = "test";
        Pageable pageable = Pageable.ofSize(30);
        List<Member> expectedResult = List.of(createMember(1L));
        given(memberRepository.searchByKeyword(searchKeyword, pageable)).willReturn(new SliceImpl<>(expectedResult, pageable, false));

        // when
        Slice<MemberDto> actualResult = sut.searchDtosByKeyword(searchKeyword, pageable);

        // then
        then(memberRepository).should().searchByKeyword(searchKeyword, pageable);
        verifyEveryMocksShoudHaveNoMoreInteractions();
        assertThat(actualResult.getNumberOfElements()).isEqualTo(expectedResult.size());
        assertThat(actualResult.getContent()).hasSize(expectedResult.size());
        for (int i = 0; i < expectedResult.size(); i++) {
            assertThat(actualResult.getContent().get(i).getId()).isEqualTo(expectedResult.get(i).getId());
        }
    }

    @DisplayName("회원 id로 회원 프로필 정보를 조회하면, 조회된 프로필 정보가 반환된다.")
    @Test
    void given_whenGettingMemberProfileInfoWithMemberId_thenReturnMemberProfileInfo() {
        // given
        long memberId = 1L;
        int numOfReviews = 62;
        String mostVisitedLocation = "연남동";
        ReviewKeywordValue mostTaggedReviewKeyword = ReviewKeywordValue.FRESH;
        FoodCategoryValue mostEatenFoodCategory = FoodCategoryValue.KOREAN;
        MemberProfileInfoDto expectedResult = createMemberProfileInfoDto(memberId, numOfReviews, mostVisitedLocation, mostTaggedReviewKeyword, mostEatenFoodCategory);
        given(memberRepository.getMemberProfileInfoById(memberId)).willReturn(expectedResult);

        // when
        MemberProfileInfoDto actualResult = sut.getMemberProfileInfoById(memberId);

        // then
        then(memberRepository).should().getMemberProfileInfoById(memberId);
        verifyEveryMocksShoudHaveNoMoreInteractions();
        assertThat(actualResult)
                .hasFieldOrPropertyWithValue("id", memberId)
                .hasFieldOrPropertyWithValue("numOfReviews", numOfReviews)
                .hasFieldOrPropertyWithValue("influence", 0)
                .hasFieldOrPropertyWithValue("numOfFollowers", 0)
                .hasFieldOrPropertyWithValue("numOfFollowings", 0)
                .hasFieldOrPropertyWithValue("mostVisitedLocation", mostVisitedLocation)
                .hasFieldOrPropertyWithValue("mostTaggedReviewKeyword", mostTaggedReviewKeyword)
                .hasFieldOrPropertyWithValue("mostEatenFoodCategory", mostEatenFoodCategory);
    }

    @DisplayName("프로필 이미지를 제외하고 수정할 회원 정보가 주어지고, 회원 정보를 수정하면, 주어진 정보로 회원 정보가 수정된다.")
    @Test
    void givenMemberUpdateInfoWithoutProfileImage_whenUpdatingMemberInfo_thenUpdate() {
        // given
        long memberId = 1L;
        Member findMember = createMember(memberId);
        MemberUpdateRequest memberUpdateInfo = new MemberUpdateRequest("update", LocalDate.of(2020, 1, 1), Gender.ETC, null);
        given(memberRepository.findByIdAndDeletedAtNull(memberId)).willReturn(Optional.of(findMember));

        // when
        MemberDto updatedMemberDto = sut.update(memberId, memberUpdateInfo);

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
        MemberUpdateRequest memberUpdateInfo = new MemberUpdateRequest("update", LocalDate.of(2020, 1, 1), Gender.ETC, createMockMultipartFile());
        given(memberRepository.findByIdAndDeletedAtNull(memberId)).willReturn(Optional.of(findMember));
        given(profileImageService.upload(any(Member.class), eq(memberUpdateInfo.getProfileImage()))).willReturn(createProfileImage(10L));

        // when
        MemberDto updatedMemberDto = sut.update(memberId, memberUpdateInfo);

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
        Member member = createMember(memberId);
        List<FoodCategoryValue> foodCategories = List.of(KOREAN, WESTERN, CAFE_DESERT);
        List<FavoriteFoodCategory> favoriteFoodCategories = List.of(
                createFavoriteFoodCategory(100L, member, KOREAN),
                createFavoriteFoodCategory(101L, member, WESTERN),
                createFavoriteFoodCategory(102L, member, CAFE_DESERT)
        );
        given(memberRepository.findByIdAndDeletedAtNull(memberId)).willReturn(Optional.of(member));
        willDoNothing().given(favoriteFoodCategoryRepository).deleteAll(member.getFavoriteFoodCategories());
        given(favoriteFoodCategoryRepository.saveAll(ArgumentMatchers.<List<FavoriteFoodCategory>>any())).willReturn(favoriteFoodCategories);

        // when
        MemberDto updatedMemberDto = sut.updateFavoriteFoodCategories(memberId, foodCategories);

        // then
        then(memberRepository).should().findByIdAndDeletedAtNull(memberId);
        then(favoriteFoodCategoryRepository).should().deleteAll(member.getFavoriteFoodCategories());
        then(favoriteFoodCategoryRepository).should().saveAll(ArgumentMatchers.<List<FavoriteFoodCategory>>any());
        then(memberRepository).shouldHaveNoMoreInteractions();
        then(favoriteFoodCategoryRepository).shouldHaveNoMoreInteractions();
        assertThat(updatedMemberDto.getFavoriteFoodCategories()).contains(KOREAN, WESTERN, CAFE_DESERT);
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
        willDoNothing().given(memberRepository).flush();
        given(memberDeletionSurveyRepository.save(any(MemberDeletionSurvey.class)))
                .willReturn(MemberTestUtils.createMemberDeletionSurvey(findMember, surveyType));

        // when
        MemberDeletionSurveyDto surveyResult = sut.delete(memberId, surveyType);

        // then
        then(memberRepository).should().findByIdAndDeletedAtNull(memberId);
        then(termsInfoRepository).should().delete(memberTermsInfo);
        then(memberRepository).should().flush();
        then(memberDeletionSurveyRepository).should().save(any(MemberDeletionSurvey.class));

        then(memberRepository).shouldHaveNoMoreInteractions();
        then(termsInfoRepository).shouldHaveNoMoreInteractions();
        then(memberDeletionSurveyRepository).shouldHaveNoMoreInteractions();

        assertThat(findMember.getTermsInfo()).isNull(); // 회원의 약관동의 정보가 null이 된다.
        assertThat(surveyResult.getSurveyType()).isEqualTo(surveyType);
    }

    @DisplayName("이미 삭제된 회원을 삭제하면, 에러가 발생한다.")
    @Test
    void givenDeletedMember_whenDeleting_thenThrowException() {
        // given
        long memberId = 1L;
        MemberDeletionSurveyType surveyType = MemberDeletionSurveyType.NOT_TRUST;
        Member findMember = createDeletedMember(memberId);
        given(memberRepository.findByIdAndDeletedAtNull(memberId)).willReturn(Optional.of(findMember));

        // when
        Throwable t = catchThrowable(() -> sut.delete(memberId, surveyType));

        // then
        then(memberRepository).should().findByIdAndDeletedAtNull(memberId);

        then(memberRepository).shouldHaveNoMoreInteractions();
        then(termsInfoRepository).shouldHaveNoMoreInteractions();
        then(memberDeletionSurveyRepository).shouldHaveNoInteractions();

        assertThat(t).isInstanceOf(MemberNotFoundException.class);
    }

    private void verifyEveryMocksShoudHaveNoMoreInteractions() {
        then(profileImageService).shouldHaveNoMoreInteractions();
        then(memberRepository).shouldHaveNoMoreInteractions();
        then(termsInfoRepository).shouldHaveNoMoreInteractions();
        then(memberDeletionSurveyRepository).shouldHaveNoMoreInteractions();
        then(favoriteFoodCategoryRepository).shouldHaveNoMoreInteractions();
    }


}
