package com.zelusik.eatery.unit.domain.profile_image.service;

import com.zelusik.eatery.domain.member.constant.Gender;
import com.zelusik.eatery.domain.member.constant.LoginType;
import com.zelusik.eatery.domain.member.constant.RoleType;
import com.zelusik.eatery.domain.member.entity.Member;
import com.zelusik.eatery.domain.profile_image.entity.ProfileImage;
import com.zelusik.eatery.domain.profile_image.repository.ProfileImageRepository;
import com.zelusik.eatery.domain.profile_image.service.ProfileImageQueryService;
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
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;

@DisplayName("[Unit] Service(Query) - Profile image")
@ExtendWith(MockitoExtension.class)
class ProfileImageQueryServiceTest {

    @InjectMocks
    private ProfileImageQueryService sut;

    @Mock
    private ProfileImageRepository profileImageRepository;

    @DisplayName("주어진 회원에 해당되는 profile image를 조회하면, 조회된 profile image의 optional 객체를 반환한다.")
    @Test
    void givenMember_whenFindingByMember_thenReturnOptionalProfileImage() {
        // given
        long memberId = 1L;
        Member member = createMember(memberId);
        ProfileImage profileImage = createProfileImage(member, 10L);
        given(profileImageRepository.findByMemberAndDeletedAtIsNull(member)).willReturn(Optional.of(profileImage));

        // when
        Optional<ProfileImage> findProfileImage = sut.findOptionalByMember(member);

        // then
        then(profileImageRepository).should().findByMemberAndDeletedAtIsNull(member);
        then(profileImageRepository).shouldHaveNoMoreInteractions();
        assertThat(findProfileImage.isPresent()).isTrue();
    }

    private Member createMember(long memberId) {
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
}