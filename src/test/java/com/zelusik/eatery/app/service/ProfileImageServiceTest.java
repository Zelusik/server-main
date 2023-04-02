package com.zelusik.eatery.app.service;

import com.zelusik.eatery.app.domain.member.Member;
import com.zelusik.eatery.app.domain.member.ProfileImage;
import com.zelusik.eatery.app.dto.ImageDto;
import com.zelusik.eatery.app.repository.member.ProfileImageRepository;
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

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
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
        ProfileImage expectedResult = MemberTestUtils.createProfileImage();
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
}