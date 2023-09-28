package com.zelusik.eatery.domain.profile_image.service;

import com.zelusik.eatery.domain.member.entity.Member;
import com.zelusik.eatery.domain.profile_image.entity.ProfileImage;
import com.zelusik.eatery.domain.profile_image.repository.ProfileImageRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@RequiredArgsConstructor
@Transactional(readOnly = true)
@Service
public class ProfileImageQueryService {

    private final ProfileImageRepository profileImageRepository;

    /**
     * Member에 해당하는 ProfileImage를 조회한다.
     *
     * @param member Member
     * @return 조회된 ProfileImage
     */
    public Optional<ProfileImage> findOptionalByMember(Member member) {
        return profileImageRepository.findByMemberAndDeletedAtIsNull(member);
    }
}
