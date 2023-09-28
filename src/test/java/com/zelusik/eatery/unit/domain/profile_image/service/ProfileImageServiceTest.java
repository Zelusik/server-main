package com.zelusik.eatery.unit.domain.profile_image.service;

import com.zelusik.eatery.domain.member.constant.Gender;
import com.zelusik.eatery.domain.member.constant.LoginType;
import com.zelusik.eatery.domain.member.constant.RoleType;
import com.zelusik.eatery.domain.member.entity.Member;
import com.zelusik.eatery.domain.profile_image.entity.ProfileImage;
import com.zelusik.eatery.domain.profile_image.repository.ProfileImageRepository;
import com.zelusik.eatery.global.file.service.S3FileService;
import com.zelusik.eatery.domain.profile_image.service.ProfileImageService;
import com.zelusik.eatery.global.file.dto.S3ImageDto;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.*;

@DisplayName("[Unit] ProfileImage Service")
@ExtendWith(MockitoExtension.class)
class ProfileImageServiceTest {

    @InjectMocks
    private ProfileImageService sut;

    @Mock
    private S3FileService s3FileService;
    @Mock
    private ProfileImageRepository profileImageRepository;

    @DisplayName("프로필 이미지(원본, 썸네일)이 주어지면, 이미지를 업로드한다.")
    @Test
    void givenProfileImages_whenUploading_thenUploadImages() {
        // given
        long memberId = 1L;
        Member member = createMember(memberId);
        MockMultipartFile profileImageForUpdate = createMockMultipartFile();
        ProfileImage expectedResult = createProfileImage(member, 10L);
        given(s3FileService.uploadImageWithResizing(eq(profileImageForUpdate), any(String.class))).willReturn(createS3ImageDto());
        given(profileImageRepository.save(any(ProfileImage.class))).willReturn(expectedResult);

        // when
        ProfileImage actualResult = sut.upload(member, profileImageForUpdate);

        // then
        then(s3FileService).should().uploadImageWithResizing(eq(profileImageForUpdate), any(String.class));
        then(profileImageRepository).should().save(any(ProfileImage.class));
        assertThat(actualResult).isNotNull();
        assertThat(actualResult.getId()).isEqualTo(expectedResult.getId());
    }

    @DisplayName("주어진 회원에 해당되는 profile image를 조회하면, 조회된 profile image의 optional 객체를 반환한다.")
    @Test
    void givenMember_whenFindingByMember_thenReturnOptionalProfileImage() {
        // given
        long memberId = 1L;
        Member member = createMember(memberId);
        ProfileImage profileImage = createProfileImage(member, 10L);
        given(profileImageRepository.findByMemberAndDeletedAtIsNull(member)).willReturn(Optional.of(profileImage));

        // when
        Optional<ProfileImage> findProfileImage = sut.findByMember(member);

        // then
        then(profileImageRepository).should().findByMemberAndDeletedAtIsNull(member);
        then(profileImageRepository).shouldHaveNoMoreInteractions();
        assertThat(findProfileImage.isPresent()).isTrue();
    }

    @DisplayName("주어진 profile image를 soft delete하면 deletedAt 필드가 현재시각으로 update된다.")
    @Test
    void givenProfileImage_whenSoftDeleting_thenUpdateDeletedAt() {
        // given
        ProfileImage profileImage = createProfileImage(createMember(1L), 10L);
        willDoNothing().given(profileImageRepository).flush();

        // when
        sut.softDelete(profileImage);

        // then
        then(profileImageRepository).should().flush();
        assertThat(profileImage.getDeletedAt()).isNotNull();
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

    private S3ImageDto createS3ImageDto() {
        return S3ImageDto.of(
                "originalFileName",
                "storedFileName",
                "url",
                "thumbnailStoredFileName",
                "thumbnailUrl"
        );
    }

    public static MockMultipartFile createMockMultipartFile() {
        return new MockMultipartFile(
                "test",
                "test.txt",
                MediaType.TEXT_PLAIN_VALUE,
                "test".getBytes()
        );
    }
}