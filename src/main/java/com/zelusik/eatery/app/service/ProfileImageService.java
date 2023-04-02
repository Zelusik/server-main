package com.zelusik.eatery.app.service;

import com.zelusik.eatery.app.domain.member.Member;
import com.zelusik.eatery.app.domain.member.ProfileImage;
import com.zelusik.eatery.app.dto.ImageDto;
import com.zelusik.eatery.app.dto.file.S3FileDto;
import com.zelusik.eatery.app.repository.member.ProfileImageRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Transactional(readOnly = true)
@Service
public class ProfileImageService {

    private final FileService fileService;
    private final ProfileImageRepository profileImageRepository;

    private static final String DIR_PATH = "member/";

    @Transactional
    public ProfileImage upload(Member member, ImageDto profileImage) {
        S3FileDto image = fileService.uploadFile(profileImage.getImage(), DIR_PATH);
        S3FileDto thumbnailImage = fileService.uploadFile(profileImage.getThumbnailImage(), DIR_PATH + "thumbnail/");
        return profileImageRepository.save(ProfileImage.of(
                member,
                image.getOriginalName(),
                image.getStoredName(),
                image.getUrl(),
                thumbnailImage.getStoredName(),
                thumbnailImage.getUrl()
        ));
    }
}
