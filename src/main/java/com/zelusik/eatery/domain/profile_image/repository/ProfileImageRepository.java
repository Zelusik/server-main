package com.zelusik.eatery.domain.profile_image.repository;

import com.zelusik.eatery.domain.member.entity.Member;
import com.zelusik.eatery.domain.profile_image.entity.ProfileImage;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ProfileImageRepository extends JpaRepository<ProfileImage, Long> {

    Optional<ProfileImage> findByMemberAndDeletedAtIsNull(Member member);
}
