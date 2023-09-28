package com.zelusik.eatery.unit.domain.member.service;

import com.zelusik.eatery.domain.favorite_food_category.entity.FavoriteFoodCategory;
import com.zelusik.eatery.domain.favorite_food_category.repository.FavoriteFoodCategoryRepository;
import com.zelusik.eatery.domain.member.constant.Gender;
import com.zelusik.eatery.domain.member.constant.LoginType;
import com.zelusik.eatery.domain.member.constant.RoleType;
import com.zelusik.eatery.domain.member.dto.MemberDto;
import com.zelusik.eatery.domain.member.dto.request.MemberUpdateRequest;
import com.zelusik.eatery.domain.member.entity.Member;
import com.zelusik.eatery.domain.member.exception.MemberNotFoundException;
import com.zelusik.eatery.domain.member.repository.MemberRepository;
import com.zelusik.eatery.domain.member.service.MemberCommandService;
import com.zelusik.eatery.domain.member.service.MemberQueryService;
import com.zelusik.eatery.domain.member_deletion_survey.constant.MemberDeletionSurveyType;
import com.zelusik.eatery.domain.member_deletion_survey.dto.MemberDeletionSurveyDto;
import com.zelusik.eatery.domain.member_deletion_survey.entity.MemberDeletionSurvey;
import com.zelusik.eatery.domain.member_deletion_survey.repository.MemberDeletionSurveyRepository;
import com.zelusik.eatery.domain.profile_image.entity.ProfileImage;
import com.zelusik.eatery.domain.profile_image.service.ProfileImageCommandService;
import com.zelusik.eatery.domain.profile_image.service.ProfileImageQueryService;
import com.zelusik.eatery.domain.terms_info.service.TermsInfoService;
import com.zelusik.eatery.global.common.constant.EateryConstants;
import com.zelusik.eatery.global.common.constant.FoodCategoryValue;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentMatchers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static com.zelusik.eatery.global.common.constant.FoodCategoryValue.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowable;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.*;

@DisplayName("[Unit] Service(Command) - Member")
@ExtendWith(MockitoExtension.class)
class MemberCommandServiceTest {

    @InjectMocks
    private MemberCommandService sut;

    @Mock
    private MemberQueryService memberQueryService;
    @Mock
    private ProfileImageCommandService profileImageCommandService;
    @Mock
    private ProfileImageQueryService profileImageQueryService;
    @Mock
    private TermsInfoService termsInfoService;
    @Mock
    private MemberRepository memberRepository;
    @Mock
    private MemberDeletionSurveyRepository memberDeletionSurveyRepository;
    @Mock
    private FavoriteFoodCategoryRepository favoriteFoodCategoryRepository;

    @DisplayName("회원 정보가 주어지면 회원가입을 진행한 후 등록된 회원 정보를 return한다.")
    @Test
    void givenMemberInfo_whenSignUp_thenSaveAndReturnMember() {
        // given
        MemberDto memberInfo = createNewMemberDto("socialUid", Set.of(RoleType.USER));
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

    @DisplayName("프로필 이미지를 제외하고 수정할 회원 정보가 주어지고, 회원 정보를 수정하면, 주어진 정보로 회원 정보가 수정된다.")
    @Test
    void givenMemberUpdateInfoWithoutProfileImage_whenUpdatingMemberInfo_thenUpdate() {
        // given
        long memberId = 1L;
        Member member = createMember(memberId);
        MemberUpdateRequest memberUpdateInfo = new MemberUpdateRequest("update", LocalDate.of(2020, 1, 1), Gender.ETC, null);
        given(memberQueryService.findById(memberId)).willReturn(member);

        // when
        MemberDto updatedMemberDto = sut.update(memberId, memberUpdateInfo);

        // then
        then(memberQueryService).should().findById(memberId);
        assertThat(updatedMemberDto.getNickname()).isEqualTo(memberUpdateInfo.getNickname());
        assertThat(updatedMemberDto.getBirthDay()).isEqualTo(memberUpdateInfo.getBirthDay());
        assertThat(updatedMemberDto.getGender()).isEqualTo(memberUpdateInfo.getGender());
    }

    @DisplayName("수정할 회원 정보가 주어지고, 회원 정보를 수정하면, 주어진 정보로 회원 정보가 수정된다.")
    @Test
    void givenMemberUpdateInfo_whenUpdatingMemberInfo_thenUpdate() {
        // given
        long memberId = 1L;
        Member member = createMember(memberId);
        MemberUpdateRequest memberUpdateInfo = new MemberUpdateRequest("update", LocalDate.of(2020, 1, 1), Gender.ETC, createMockMultipartFile());
        ProfileImage oldProfileImage = createProfileImage(member, 2L);
        given(memberQueryService.findById(memberId)).willReturn(member);
        given(profileImageQueryService.findOptionalByMember(member)).willReturn(Optional.of(oldProfileImage));
        willDoNothing().given(profileImageCommandService).softDelete(oldProfileImage);
        given(profileImageCommandService.upload(any(Member.class), eq(memberUpdateInfo.getProfileImage()))).willReturn(createProfileImage(member, 10L));

        // when
        MemberDto updatedMemberDto = sut.update(memberId, memberUpdateInfo);

        // then
        then(memberQueryService).should().findById(memberId);
        then(profileImageQueryService).should().findOptionalByMember(member);
        then(profileImageCommandService).should().softDelete(oldProfileImage);
        then(profileImageCommandService).should().upload(any(Member.class), eq(memberUpdateInfo.getProfileImage()));
        verifyEveryMocksShouldHaveNoMoreInteractions();
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
        List<FoodCategoryValue> foodCategories = List.of(KOREAN, WESTERN, CAFE_DESSERT);
        List<FavoriteFoodCategory> favoriteFoodCategories = List.of(
                createFavoriteFoodCategory(100L, member, KOREAN),
                createFavoriteFoodCategory(101L, member, WESTERN),
                createFavoriteFoodCategory(102L, member, CAFE_DESSERT)
        );
        given(memberQueryService.findById(memberId)).willReturn(member);
        willDoNothing().given(favoriteFoodCategoryRepository).deleteAll(member.getFavoriteFoodCategories());
        given(favoriteFoodCategoryRepository.saveAll(ArgumentMatchers.<List<FavoriteFoodCategory>>any())).willReturn(favoriteFoodCategories);

        // when
        MemberDto updatedMemberDto = sut.updateFavoriteFoodCategories(memberId, foodCategories);

        // then
        then(memberQueryService).should().findById(memberId);
        then(favoriteFoodCategoryRepository).should().deleteAll(member.getFavoriteFoodCategories());
        then(favoriteFoodCategoryRepository).should().saveAll(ArgumentMatchers.<List<FavoriteFoodCategory>>any());
        then(memberRepository).shouldHaveNoMoreInteractions();
        then(favoriteFoodCategoryRepository).shouldHaveNoMoreInteractions();
        assertThat(updatedMemberDto.getFavoriteFoodCategories()).contains(KOREAN, WESTERN, CAFE_DESSERT);
    }

    @DisplayName("회원 탈퇴를 하면, 회원과 약관 동의 정보를 삭제하고 탈퇴 설문 정보를 반환한다.")
    @Test
    void given_whenDeleteMember_thenDeleteMemberAndReturnSurvey() {
        // given
        long memberId = 1L;
        MemberDeletionSurveyType surveyType = MemberDeletionSurveyType.NOT_TRUST;
        Member findMember = createMember(memberId);
        given(memberQueryService.findById(memberId)).willReturn(findMember);
        willDoNothing().given(termsInfoService).deleteByMemberId(memberId);
        given(memberDeletionSurveyRepository.save(any(MemberDeletionSurvey.class)))
                .willReturn(createMemberDeletionSurvey(11L, findMember, surveyType));

        // when
        MemberDeletionSurveyDto surveyResult = sut.delete(memberId, surveyType);

        // then
        then(memberQueryService).should().findById(memberId);
        then(termsInfoService).should().deleteByMemberId(memberId);
        then(memberDeletionSurveyRepository).should().save(any(MemberDeletionSurvey.class));
        verifyEveryMocksShouldHaveNoMoreInteractions();
        assertThat(surveyResult.getSurveyType()).isEqualTo(surveyType);
    }

    @DisplayName("이미 삭제된 회원을 삭제하면, 에러가 발생한다.")
    @Test
    void givenDeletedMember_whenDeleting_thenThrowException() {
        // given
        long memberId = 1L;
        MemberDeletionSurveyType surveyType = MemberDeletionSurveyType.NOT_TRUST;
        Member deletedMember = createDeletedMember(memberId);
        given(memberQueryService.findById(memberId)).willReturn(deletedMember);

        // when
        Throwable t = catchThrowable(() -> sut.delete(memberId, surveyType));

        // then
        then(memberQueryService).should().findById(memberId);
        then(memberRepository).shouldHaveNoMoreInteractions();
        then(termsInfoService).shouldHaveNoMoreInteractions();
        then(memberDeletionSurveyRepository).shouldHaveNoInteractions();

        assertThat(t).isInstanceOf(MemberNotFoundException.class);
    }

    private void verifyEveryMocksShouldHaveNoMoreInteractions() {
        then(profileImageQueryService).shouldHaveNoMoreInteractions();
        then(profileImageCommandService).shouldHaveNoMoreInteractions();
        then(termsInfoService).shouldHaveNoMoreInteractions();
        then(memberRepository).shouldHaveNoMoreInteractions();
        then(memberDeletionSurveyRepository).shouldHaveNoMoreInteractions();
        then(favoriteFoodCategoryRepository).shouldHaveNoMoreInteractions();
    }

    private Member createDeletedMember(long memberId) {
        return Member.of(
                memberId,
                "profile image url",
                "profile thunmbnail image url",
                "social user id" + memberId,
                LoginType.KAKAO,
                Set.of(RoleType.USER),
                "email",
                "nickname",
                LocalDate.of(2000, 1, 1),
                20,
                Gender.MALE,
                LocalDateTime.now(),
                LocalDateTime.now(),
                LocalDateTime.now()
        );
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

    private ProfileImage createProfileImage(Member member, long profileImageId) {
        return ProfileImage.of(
                profileImageId,
                member,
                "originalFilename",
                "storedFilename",
                "url",
                "thumbnailStoredFilename",
                "thumbnailUrl",
                LocalDateTime.of(2023, 1, 1, 0, 0),
                LocalDateTime.of(2023, 1, 1, 0, 0),
                null
        );
    }

    private FavoriteFoodCategory createFavoriteFoodCategory(long id, Member member, FoodCategoryValue foodCategoryValue) {
        return FavoriteFoodCategory.of(id, member, foodCategoryValue);
    }

    private MemberDeletionSurvey createMemberDeletionSurvey(long memberDeletionSurveyId, Member member, MemberDeletionSurveyType surveyType) {
        return MemberDeletionSurvey.of(
                memberDeletionSurveyId,
                member,
                surveyType,
                LocalDateTime.of(2023, 1, 1, 0, 0),
                LocalDateTime.of(2023, 1, 1, 0, 0)
        );
    }

    private MemberDto createNewMemberDto(String socialUid, Set<RoleType> roleTypes) {
        return new MemberDto(
                EateryConstants.defaultProfileImageUrl,
                EateryConstants.defaultProfileThumbnailImageUrl,
                socialUid,
                LoginType.KAKAO,
                roleTypes,
                "email",
                "nickname" + socialUid,
                null,
                null
        );
    }

    private MockMultipartFile createMockMultipartFile() {
        return new MockMultipartFile(
                "test",
                "test.txt",
                MediaType.TEXT_PLAIN_VALUE,
                "test".getBytes()
        );
    }
}
