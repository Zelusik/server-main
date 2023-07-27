package com.zelusik.eatery.service;

import com.zelusik.eatery.domain.member.Member;
import com.zelusik.eatery.domain.member.ProfileImage;
import com.zelusik.eatery.repository.member.ProfileImageRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.Optional;

@RequiredArgsConstructor
@Transactional(readOnly = true)
@Service
public class ProfileImageService {

    private final FileService fileService;
    private final ProfileImageRepository profileImageRepository;

    private static final String DIR_PATH = "member/";

    /**
     * Member profile image entity를 생성하여 DB에 저장하고 S3에 upload한다.
     *
     * @param member                Member
     * @param profileImageForUpdate Profile image
     * @return 생성된 ProfileImage entity
     */
    @Transactional
    public ProfileImage upload(Member member, MultipartFile profileImageForUpdate) {
        S3ImageDto imageDto = fileService.uploadImageWithResizing(profileImageForUpdate, DIR_PATH);
        return profileImageRepository.save(ProfileImage.of(
                member,
                imageDto.getOriginalName(),
                imageDto.getStoredName(),
                imageDto.getUrl(),
                imageDto.getThumbnailStoredName(),
                imageDto.getThumbnailUrl()
        ));
    }

    /**
     * Member에 해당하는 ProfileImage를 조회한다.
     *
     * @param member Member
     * @return 조회된 ProfileImage
     */
    public Optional<ProfileImage> findByMember(Member member) {
        return profileImageRepository.findByMemberAndDeletedAtIsNull(member);
    }

    /**
     * <p>ProfileImage를 soft delete한다.
     * <p>ProfileImage의 deletedAt이 현재시각으로 update된다.
     *
     * @param profileImage soft delete 하고자 하는 ProfileImage 객체
     */
    @Transactional
    public void softDelete(ProfileImage profileImage) {
        profileImage.softDelete();
        profileImageRepository.flush();
    }
}
