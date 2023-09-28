package com.zelusik.eatery.unit.domain.member.service;

import com.zelusik.eatery.domain.member.constant.Gender;
import com.zelusik.eatery.domain.member.constant.LoginType;
import com.zelusik.eatery.domain.member.constant.RoleType;
import com.zelusik.eatery.domain.member.dto.MemberDto;
import com.zelusik.eatery.domain.member.dto.MemberProfileInfoDto;
import com.zelusik.eatery.domain.member.entity.Member;
import com.zelusik.eatery.domain.member.exception.MemberIdNotFoundException;
import com.zelusik.eatery.domain.member.repository.MemberRepository;
import com.zelusik.eatery.domain.member.service.MemberQueryService;
import com.zelusik.eatery.domain.review.constant.ReviewKeywordValue;
import com.zelusik.eatery.global.common.constant.FoodCategoryValue;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.SliceImpl;
import org.springframework.lang.NonNull;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowable;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;

@DisplayName("[Unit] Service(Query) - Member")
@ExtendWith(MockitoExtension.class)
class MemberQueryServiceTest {

    @InjectMocks
    private MemberQueryService sut;

    @Mock
    private MemberRepository memberRepository;

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
        then(memberRepository).shouldHaveNoMoreInteractions();
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
        then(memberRepository).shouldHaveNoMoreInteractions();
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

    @NonNull
    private MemberProfileInfoDto createMemberProfileInfoDto(long memberId, int numOfReviews, String mostVisitedLocation, ReviewKeywordValue mostTaggedReviewKeyword, FoodCategoryValue mostEatenFoodCategory) {
        return MemberProfileInfoDto.of(
                createMember(memberId),
                numOfReviews,
                mostVisitedLocation,
                mostTaggedReviewKeyword,
                mostEatenFoodCategory
        );
    }
}
