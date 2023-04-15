package com.zelusik.eatery.service;

import com.zelusik.eatery.domain.member.Member;
import com.zelusik.eatery.domain.member.ProfileImage;
import com.zelusik.eatery.dto.ImageDto;
import com.zelusik.eatery.repository.member.ProfileImageRepository;
import com.zelusik.eatery.util.MemberTestUtils;
import com.zelusik.eatery.util.MultipartFileTestUtils;
import com.zelusik.eatery.util.S3FileTestUtils;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.multipart.MultipartFile;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.*;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@DisplayName("[Service] ProfileImage")
@ExtendWith(MockitoExtension.class)
class ProfileImageServiceTest {

    @InjectMocks
    private ProfileImageService sut;

    @Mock
    private FileService fileService;
    @Mock
    private ProfileImageRepository profileImageRepository;

    @DisplayName("프로필 이미지(원본, 썸네일)이 주어지면, 이미지를 업로드한다.")
    @Test
    void givenProfileImages_whenUploading_thenUploadImages() {
        // given
        long memberId = 1L;
        Member member = MemberTestUtils.createMember(memberId);
        ImageDto imageDto = MultipartFileTestUtils.createMockImageDto();
        ProfileImage expectedResult = MemberTestUtils.createProfileImage(10L);
        given(fileService.uploadFile(any(MultipartFile.class), any(String.class)))
                .willReturn(S3FileTestUtils.createS3FileDto());
        given(profileImageRepository.save(any(ProfileImage.class))).willReturn(expectedResult);

        // when
        ProfileImage actualResult = sut.upload(member, imageDto);

        // then
        verify(fileService, times(2)).uploadFile(any(MultipartFile.class), any(String.class));
        then(profileImageRepository).should().save(any(ProfileImage.class));
        assertThat(actualResult).isNotNull();
        assertThat(actualResult.getId()).isEqualTo(expectedResult.getId());
    }

    @DisplayName("주어진 회원에 해당되는 profile image를 조회하면, 조회된 profile image의 optional 객체를 반환한다.")
    @Test
    void givenMember_whenFindingByMember_thenReturnOptionalProfileImage() {
        // given
        long memberId = 1L;
        Member member = MemberTestUtils.createMember(memberId);
        ProfileImage profileImage = MemberTestUtils.createProfileImage(member, 10L);
        given(profileImageRepository.findByMemberAndDeletedAtIsNull(member))
                .willReturn(Optional.of(profileImage));

        // when
        Optional<ProfileImage> findProfileImage = sut.findEntityByMember(member);

        // then
        then(profileImageRepository).should().findByMemberAndDeletedAtIsNull(member);
        then(profileImageRepository).shouldHaveNoMoreInteractions();
        assertThat(findProfileImage.isPresent()).isTrue();
    }

    @DisplayName("주어진 profile image를 soft delete하면 deletedAt 필드가 현재시각으로 update된다.")
    @Test
    void givenProfileImage_whenSoftDeleting_thenUpdateDeletedAt() {
        // given
        ProfileImage profileImage = MemberTestUtils.createProfileImage(10L);
        willDoNothing().given(profileImageRepository).flush();

        // when
        sut.softDelete(profileImage);

        // then
        then(profileImageRepository).should().flush();
        assertThat(profileImage.getDeletedAt()).isNotNull();
    }
}